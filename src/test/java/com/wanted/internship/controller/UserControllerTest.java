package com.wanted.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.internship.dto.user.LoginRequest;
import com.wanted.internship.dto.user.SignupRequest;
import com.wanted.internship.entity.Authority;
import com.wanted.internship.entity.User;
import com.wanted.internship.repository.UserRepository;
import com.wanted.internship.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class UserControllerTest {

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

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext,
               RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentationContextProvider)
                        .operationPreprocessors()
                        .withRequestDefaults(modifyUris().host("localhost").port(8080), prettyPrint())
                        .withResponseDefaults(modifyUris().host("localhost").port(8080), prettyPrint()))
                .build();
    }

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

        // then
        resultActions.andExpect(status().isCreated());

        User findUser = userRepository.findByEmail(signupRequest.email()).orElseThrow();

        assertAll(
                () -> assertThat(findUser).isNotNull(),
                () -> assertThat(findUser.getUserRole()).isEqualTo(Authority.ROLE_MEMBER)
        );

        resultActions.andDo(
                document("signup",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("content type"),
                                headerWithName(CONTENT_LENGTH).description("length of header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("회원 가입 이메일"),
                                fieldWithPath("password").description("회원 가입 비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("message").description("정상 회원 가입 시 나타나는 메세지")
                        )
                )
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
        resultActions.andDo(
                document("login",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("content type"),
                                headerWithName(CONTENT_LENGTH).description("length of header")
                        ),
                        requestFields(
                                fieldWithPath("email").description("로그인 이메일"),
                                fieldWithPath("password").description("로그인 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName("Authorization").description("Access Token"),
                                headerWithName("RefreshToken").description("Refresh Token")
                        )
                )
        );
    }
}
