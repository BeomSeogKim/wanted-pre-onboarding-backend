package com.wanted.internship.config;

import com.wanted.internship.security.TokenProvider;
import com.wanted.internship.security.UserDetailsServiceImpl;
import com.wanted.internship.security.exception.AccessDeniedHandlerException;
import com.wanted.internship.security.exception.AuthenticationEntryPointException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String SECRET_KEY;
    private final TokenProvider tokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationEntryPointException authenticationEntryPointException;
    private final AccessDeniedHandlerException accessDeniedHandlerException;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
        ;

        httpSecurity
                .sessionManagement(
                        sessionManagement ->
                                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
        ;


        httpSecurity
                .exceptionHandling(
                        exceptionHandling -> {
                            exceptionHandling.authenticationEntryPoint(authenticationEntryPointException);
                            exceptionHandling.accessDeniedHandler(accessDeniedHandlerException);
                        }
                )

        ;

        httpSecurity
                .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));
        return httpSecurity
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers(
                                        antMatcher(HttpMethod.POST, "/api/users"),
                                        antMatcher(HttpMethod.POST, "/api/users/login")
                                ).permitAll()
                                .requestMatchers(
                                        antMatcher(HttpMethod.GET, "/api/posts/**")
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .build();
    }

}
