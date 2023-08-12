package com.wanted.internship.service;

import com.wanted.internship.dto.user.SignupRequest;
import com.wanted.internship.dto.user.SignupResponse;
import com.wanted.internship.entity.User;
import com.wanted.internship.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signUp(SignupRequest signupRequest) {
        validateEmail(signupRequest);
        validatePassword(signupRequest);
        checkEmailDuplication(signupRequest);
        String encodedPassword = passwordEncoder.encode(signupRequest.password());
        User user = User.of(signupRequest.email(), encodedPassword);
        userRepository.save(user);
        return new SignupResponse("정상적으로 회원가입이 완료되었습니다.");
    }

    private static void validateEmail(SignupRequest signupRequest) {
        if (!signupRequest.email().contains("@")) {
            throw new IllegalArgumentException("이메일에는 @가 포함되어야 합니다.");
        }
    }

    private static void validatePassword(SignupRequest signupRequest) {
        if (signupRequest.password().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상입니다.");
        }
    }

    private void checkEmailDuplication(SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.email()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 Email 입니다. 다른 Email로 가입 부탁드립니다.")            ;
        }
    }
}
