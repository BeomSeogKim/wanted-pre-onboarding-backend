package com.wanted.internship.service;

import com.wanted.internship.dto.post.PostReadResponse;
import com.wanted.internship.entity.Post;
import com.wanted.internship.entity.User;
import com.wanted.internship.repository.PostRepository;
import com.wanted.internship.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("게시글 단건 조회 검증")
    void findById() {

        // given
        String email = "kbs4520@naver.com", password = "password1234";
        User user = User.of(email, password);
        User savedUser = userRepository.save(user);

        Post post = Post.of("this is new content", savedUser);
        Post savedPost = postRepository.save(post);

        entityManager.flush();
        entityManager.clear();

        // when
        PostReadResponse postReadResponse = postService.findById(savedPost.getId());

        // then
        assertAll(
                () -> assertThat(postReadResponse.postId()).isEqualTo(savedPost.getId()),
                () -> assertThat(postReadResponse.userId()).isEqualTo(savedUser.getId())
        );

    }

    @Test
    @DisplayName("게시글 단건 조회시 유효하지 않은 postId의 경우 예외 발생")
    void findById_InvalidPostId() {

        // given
        String email = "kbs4520@naver.com", password = "password1234";
        User user = User.of(email, password);
        User savedUser = userRepository.save(user);

        Post post = Post.of("this is new content", savedUser);
        Post savedPost = postRepository.save(post);

        entityManager.flush();
        entityManager.clear();

        // when && then
        assertThatThrownBy(
                () -> postService.findById(savedPost.getId() + 1)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 게시글은 존재하지 않습니다. 다시한번 확인해주세요.");
    }
}
