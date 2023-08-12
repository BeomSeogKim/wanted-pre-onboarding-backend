package com.wanted.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.internship.RestDocsSupport;
import com.wanted.internship.dto.user.LoginRequest;
import com.wanted.internship.dto.user.SignupRequest;
import com.wanted.internship.entity.Authority;
import com.wanted.internship.entity.User;
import com.wanted.internship.repository.UserRepository;
import com.wanted.internship.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
class UserControllerTest extends RestDocsSupport {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserController userController;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @DisplayName("회원가입")
    void signup() throws Exception {

        // given
        SignupRequest signupRequest = new SignupRequest("kbs4520@naver.com", "password1234");

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(signupRequest))
        );

        // todo 문서화 작업
        // then
        resultActions.andExpect(status().isCreated());

        User findUser = userRepository.findByEmail(signupRequest.email()).orElseThrow();

        assertAll(
                () -> assertThat(findUser).isNotNull(),
                () -> assertThat(findUser.getUserRole()).isEqualTo(Authority.ROLE_MEMBER)
        );


    }

    @Test
    @DisplayName("로그인")
    void login() throws Exception {

        // given
        SignupRequest signupRequest = new SignupRequest("kbs4520@naver.com", "password1234");
        userService.signUp(signupRequest);
        LoginRequest loginRequest = new LoginRequest("kbs4520@naver.com", "password1234");

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/users/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest))
        );

        // then
        resultActions.andExpect(status().isNoContent());
        MvcResult mvcResult = resultActions.andReturn();
        String accessToken = mvcResult.getResponse().getHeader("Authorization");
        String refreshToken = mvcResult.getResponse().getHeader("RefreshToken");

        assertAll(
                () -> assertThat(accessToken).isNotNull(),
                () -> assertThat(refreshToken).isNotNull(),
                () -> assertThat(accessToken.startsWith("Bearer ")).isTrue()
        );

    }
}
