package com.wanted.internship.security;

import com.wanted.internship.dto.token.TokenDto;
import com.wanted.internship.entity.RefreshToken;
import com.wanted.internship.entity.User;
import com.wanted.internship.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            //30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;     //7일

    private final Key key;

    private final RefreshTokenRepository refreshTokenRepository;

    // 암호화
    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰 생성
    @Transactional
    public TokenDto generateTokenDto(com.wanted.internship.entity.User user) {
        long now = (new Date().getTime());

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(user.getEmail())
                .claim(AUTHORITIES_KEY, user.getUserRole().toString())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshTokenObject = RefreshToken.builder()
                .id(user.getEmail())
                .user(user)
                .keyValue(refreshToken)
                .build();

        refreshTokenRepository.save(refreshTokenObject);

        return new TokenDto(BEARER_PREFIX, accessToken, refreshToken, accessTokenExpiresIn.getTime());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    @Transactional(readOnly = true)
    public RefreshToken isPresentRefreshToken(User user) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUser(user);
        return optionalRefreshToken.orElse(null);
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        String token = getAccessToken(request);
        if (token == null) {
            return null;
        } else {
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            org.springframework.security.core.userdetails.User principal =
                    new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        }
    }

    private String getAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
