package com.wanted.internship.service;

import com.wanted.internship.dto.user.SignupRequest;
import com.wanted.internship.entity.Authority;
import com.wanted.internship.entity.User;
import com.wanted.internship.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;


    @Test
    @DisplayName("회원 가입 정상 검증")
    void signUp() {

        // given
        SignupRequest signupRequest = new SignupRequest("kbs4520@naver.com", "tommeo1092");

        // when
        userService.signUp(signupRequest);

        // then
        User user = userRepository.findByEmail(signupRequest.email()).orElseThrow();
        assertAll(
                () -> assertThat(user).isNotNull(),
                () -> assertThat(user.getPassword()).isNotEqualTo(signupRequest.password()),
                () -> assertThat(user.getUserRole()).isEqualTo(Authority.ROLE_MEMBER),
                () -> assertThat(user.getEmail()).isEqualTo(signupRequest.email())
        );

    }

    @Test
    @DisplayName("회원 가입 비정상 요청 시 IllegalArgument Exception 발생")
    void signUp_BadRequest() {

        // given
        SignupRequest badEmailRequest = new SignupRequest("kbs4520naver.com", "tommeo1092");
        SignupRequest badPasswordRequest = new SignupRequest("kbs4520@naver.com", "tom");

        // when && then
        assertThatThrownBy(
                () -> userService.signUp(badEmailRequest)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일에는 @가 포함되어야 합니다.");

        assertThatThrownBy(
                () -> userService.signUp(badPasswordRequest)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 최소 8자 이상입니다.");
    }

    @Test
    @DisplayName("이미 존재하는 Email일 경우 IllegalArgument Exception 발생")
    void signUp_EmailDuplicate() {

        // given
        SignupRequest signupRequest = new SignupRequest("kbs4520@naver.com", "tommeo1092");
        userService.signUp(signupRequest);

        // when && then
        assertThatThrownBy(
                () -> userService.signUp(signupRequest)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 Email 입니다. 다른 Email로 가입 부탁드립니다.");
    }
}
