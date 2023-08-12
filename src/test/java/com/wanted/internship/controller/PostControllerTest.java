package com.wanted.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.internship.RestDocsSupport;
import com.wanted.internship.dto.post.PostWriteRequest;
import com.wanted.internship.dto.user.LoginRequest;
import com.wanted.internship.dto.user.SignupRequest;
import com.wanted.internship.entity.Post;
import com.wanted.internship.entity.User;
import com.wanted.internship.repository.PostRepository;
import com.wanted.internship.repository.UserRepository;
import com.wanted.internship.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
class PostControllerTest extends RestDocsSupport {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("게시글 작성 검증")
    void write() throws Exception{

        // given
        String email = "kbs4520@naver.com";
        String password = "password1234";
        String accessToken = login(email, password);

        String content = "안녕하세요 처음 게시글을 써보는데 신기하네요..! 앞으로 잘 부탁드립니다.";
        PostWriteRequest postWriteRequest = new PostWriteRequest(content);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postWriteRequest))
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isCreated());
        Post findPost = postRepository.findAll().get(0);
        User findUser = userRepository.findByEmail(email).orElseThrow();
        assertAll(
                () -> assertThat(findPost.getContent()).isEqualTo(content),
                () -> assertThat(findPost.getUser()).isEqualTo(findUser)
        );

    }

    @Test
    @DisplayName("로그인을 하지 않고 게시글 생성 불가하다.")
    void write_NoAuthentication() throws Exception {

        // given
        String content = "안녕하세요 처음 게시글을 써보는데 신기하네요..! 앞으로 잘 부탁드립니다.";
        PostWriteRequest postWriteRequest = new PostWriteRequest(content);

        // when
        ResultActions resultActions = this.mockMvc.perform(
                post("/api/posts")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postWriteRequest))
        );

        // then
        resultActions.andExpect(status().isUnauthorized());
    }

    private String login(String email, String password) throws Exception {
        SignupRequest signupRequest = new SignupRequest(email, password);
        userService.signUp(signupRequest);

        LoginRequest loginRequest = new LoginRequest(email, password);

        return this.mockMvc.perform(
                post("/api/users/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(loginRequest))
        ).andReturn().getResponse().getHeader("Authorization");
    }
}