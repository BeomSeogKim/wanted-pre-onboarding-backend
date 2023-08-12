package com.wanted.internship.service;

import com.wanted.internship.dto.token.TokenDto;
import com.wanted.internship.dto.user.LoginRequest;
import com.wanted.internship.dto.user.SignupRequest;
import com.wanted.internship.dto.user.SignupResponse;
import com.wanted.internship.entity.User;
import com.wanted.internship.repository.UserRepository;
import com.wanted.internship.security.TokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public SignupResponse signUp(SignupRequest signupRequest) {
        validateEmail(signupRequest.email());
        validatePassword(signupRequest.password());
        checkEmailDuplication(signupRequest);
        String encodedPassword = passwordEncoder.encode(signupRequest.password());
        User user = User.of(signupRequest.email(), encodedPassword);
        userRepository.save(user);
        return new SignupResponse("정상적으로 회원가입이 완료되었습니다.");
    }

    private static void validateEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("이메일에는 @가 포함되어야 합니다.");
        }
    }

    private static void validatePassword(String password) {
        if (password.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상입니다.");
        }
    }

    private void checkEmailDuplication(SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 Email 입니다. 다른 Email로 가입 부탁드립니다.");
        }
    }

    public void login(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        validateEmail(loginRequest.email());
        validatePassword(loginRequest.password());

        User user = checkEmail(loginRequest);
        checkPassword(loginRequest, user);

        generateToken(httpServletResponse, user);
        setResponseStatus(httpServletResponse);
    }

    private User checkEmail(LoginRequest loginRequest) {
        return userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("해당 Email은 존재하지 않습니다. 다시 한번 확인 부탁드립니다.")
                );
    }

    private void checkPassword(LoginRequest loginRequest, User user) {
        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호 입니다. 다시 한번 확인 부탁드립니다.");
        }
    }

    private void generateToken(HttpServletResponse httpServletResponse, User user) {
        TokenDto tokenDto = tokenProvider.generateTokenDto(user);
        httpServletResponse.addHeader("Authorization", "Bearer " + tokenDto.accessToken());
        httpServletResponse.addHeader("RefreshToken", tokenDto.refreshToken());
    }

    private static void setResponseStatus(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpStatus.NO_CONTENT.value());
    }
}
