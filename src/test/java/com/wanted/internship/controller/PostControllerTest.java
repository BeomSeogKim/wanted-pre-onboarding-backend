package com.wanted.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.internship.dto.post.PostEditRequest;
import com.wanted.internship.dto.post.PostWriteRequest;
import com.wanted.internship.dto.user.LoginRequest;
import com.wanted.internship.dto.user.SignupRequest;
import com.wanted.internship.entity.Post;
import com.wanted.internship.entity.User;
import com.wanted.internship.repository.PostRepository;
import com.wanted.internship.repository.UserRepository;
import com.wanted.internship.service.UserService;
import jakarta.persistence.EntityManager;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class PostControllerTest {

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

    @Autowired
    EntityManager entityManager;

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
    @DisplayName("게시글 작성 검증")
    void write() throws Exception {

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

        resultActions.andDo(
                document("writePost",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("content type"),
                                headerWithName("Authorization").description("Access Token"),
                                headerWithName(CONTENT_LENGTH).description("length of content")
                        ),
                        requestFields(
                                fieldWithPath("content").description("content of post")
                        ),
                        responseFields(
                                fieldWithPath("postId").description("id of post"),
                                fieldWithPath("content").description("content of post"),
                                fieldWithPath("userId").description("id of writer")
                        )
                )
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

    @Test
    @DisplayName("게시글 전체 조회")
    void findAll() throws Exception {

        // given
        String email = "kbs4520@naver.com", password = "password1234";
        String accessToken = login(email, password);

        User user = userRepository.findByEmail(email).orElseThrow();

        for (int i = 0; i < 20; i++) {
            Post post = Post.of("content " + (i + 1), user);
            postRepository.save(post);
        }

        // when
        ResultActions resultActions = this.mockMvc.perform(
                get("/api/posts")
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk());

        resultActions.andDo(
                document("findAllPost",
                        responseFields(
                                fieldWithPath("postList[0].postId").description("id of post"),
                                fieldWithPath("postList[0].content").description("content of post"),
                                fieldWithPath("postList[0].userId").description("id of writer"),
                                fieldWithPath("firstPage").description("boolean of firstPage"),
                                fieldWithPath("lastPage").description("boolean of lastPage"),
                                fieldWithPath("currentPage").description("number of current page"),
                                fieldWithPath("totalPage").description("number of total page")
                        )
                )
        );

    }

    @Test
    @DisplayName("게시글 전체 조회의 경우 회원이 아니어도 된다.")
    void findAll_NoAuthentication() throws Exception {

        // given
        String email = "kbs4520@naver.com", password = "password1234";
        login(email, password);
        User user = userRepository.findByEmail(email).orElseThrow();

        for (int i = 0; i < 20; i++) {
            Post post = Post.of("content " + (i + 1), user);
            postRepository.save(post);
        }

        // when
        ResultActions resultActions = this.mockMvc.perform(
                get("/api/posts")
        );

        // then
        resultActions.andExpect(status().isOk());

    }

    @Test
    @DisplayName("게시글 단건 조회")
    void findById() throws Exception {

        // given
        String email = "kbs4520@naver.com", password = "password1234";
        login(email, password);
        User user = userRepository.findByEmail(email).orElseThrow();

        Post post = Post.of("this is a content for a test", user);
        Post savedPost = postRepository.save(post);

        entityManager.flush();
        entityManager.clear();

        // when
        ResultActions resultActions = this.mockMvc.perform(
                get("/api/posts/" + savedPost.getId())
        );

        // then
        resultActions.andExpect(status().isOk());

        resultActions.andDo(
                document("findPost",
                        responseFields(
                                fieldWithPath("postId").description("id of post"),
                                fieldWithPath("content").description("content of post"),
                                fieldWithPath("userId").description("id of writer")
                        )
                )
        );
    }

    @Test
    @DisplayName("게시글 수정")
    void editPost() throws Exception {

        // given
        String email = "kbs4520@naver.com", password = "password1234";

        String accessToken = login(email, password);
        User user = userRepository.findByEmail(email).orElseThrow();

        Post post = Post.of("this is a content for a test", user);
        Post savedPost = postRepository.save(post);
        PostEditRequest postEditRequest = new PostEditRequest("this is a new content");

        entityManager.flush();
        entityManager.clear();

        // when
        ResultActions resultActions = this.mockMvc.perform(
                patch("/api/posts/" + savedPost.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postEditRequest))
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk());

        Post findPost = postRepository.findById(savedPost.getId()).orElseThrow();
        assertThat(findPost.getContent()).isEqualTo("this is a new content");

        resultActions.andDo(
                document("editPost",
                        requestHeaders(
                                headerWithName(CONTENT_TYPE).description("content type"),
                                headerWithName("Authorization").description("Access Token "),
                                headerWithName(CONTENT_LENGTH).description("length of content")
                        ),
                        requestFields(
                                fieldWithPath("content").description("content to edit")
                        ),
                        responseFields(
                                fieldWithPath("postId").description("id of post"),
                                fieldWithPath("content").description("content of post")
                        )
                )
        );
    }

    @Test
    @DisplayName("게시글 수정 - 권한이 없는 경우 수정이 불가하다.")
    void editPost_NoAuthorization() throws Exception {

        // given
        String email1 = "kbs4520@naver.com", password1 = "password1234";
        String email2 = "kbs@naver.com", password2 = "password1234";

        login(email1, password1);
        String accessToken = login(email2, password2);
        User user = userRepository.findByEmail(email1).orElseThrow();

        Post post = Post.of("this is a content for a test", user);
        Post savedPost = postRepository.save(post);
        PostEditRequest postEditRequest = new PostEditRequest("this is a new content");

        entityManager.flush();
        entityManager.clear();

        // when
        ResultActions resultActions = this.mockMvc.perform(
                patch("/api/posts/" + savedPost.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(postEditRequest))
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isForbidden());

        Post findPost = postRepository.findById(savedPost.getId()).orElseThrow();
        assertThat(findPost.getContent()).isEqualTo("this is a content for a test");
    }

    @Test
    @DisplayName("게시글 삭제")
    void deletePost() throws Exception {

        // given
        String email = "kbs4520@naver.com", password = "password1234";

        String accessToken = login(email, password);
        User user = userRepository.findByEmail(email).orElseThrow();

        Post post = Post.of("this is a content for a test", user);
        Post savedPost = postRepository.save(post);

        entityManager.flush();
        entityManager.clear();

        // when
        ResultActions resultActions = this.mockMvc.perform(
                delete("/api/posts/" + savedPost.getId())
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isOk());

        assertThat(postRepository.findAll()).isEmpty();

        resultActions.andDo(
                document("deletePost",
                        requestHeaders(
                                headerWithName("Authorization").description("Access Token")
                        ),
                        responseFields(
                                fieldWithPath("message").description("message of request")
                        )
                )
        );
    }

    @Test
    @DisplayName("게시글 삭제 - 권한이 없는 경우 삭제가 불가하다.")
    void deletePost_NoAuthorization() throws Exception {

        // given
        String email1 = "kbs4520@naver.com", password1 = "password1234";
        String email2 = "kbs@naver.com", password2 = "password1234";

        login(email1, password1);
        String accessToken = login(email2, password2);
        User user = userRepository.findByEmail(email1).orElseThrow();

        Post post = Post.of("this is a content for a test", user);
        Post savedPost = postRepository.save(post);

        entityManager.flush();
        entityManager.clear();

        // when
        ResultActions resultActions = this.mockMvc.perform(
                patch("/api/posts/" + savedPost.getId())
                        .header("Authorization", accessToken)
        );

        // then
        resultActions.andExpect(status().isBadRequest());

        assertThat(postRepository.findAll()).hasSize(1);
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
