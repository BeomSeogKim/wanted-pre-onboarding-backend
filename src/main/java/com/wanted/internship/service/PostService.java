package com.wanted.internship.service;

import com.wanted.internship.dto.post.*;
import com.wanted.internship.entity.Post;
import com.wanted.internship.entity.User;
import com.wanted.internship.exception.NoAuthenticationException;
import com.wanted.internship.exception.NoAuthorityException;
import com.wanted.internship.repository.PostRepository;
import com.wanted.internship.repository.UserRepository;
import com.wanted.internship.security.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;


    @Transactional
    public PostWriteResponse write(HttpServletRequest httpServletRequest, PostWriteRequest postWriteRequest) {
        Authentication authentication = tokenProvider.getAuthentication(httpServletRequest);
        if (authentication == null) {
            throw new NoAuthenticationException("해당 권한이 유효하지 않습니다. 재 로그인 해주세요");
        }
        String email = authentication.getName();
        checkAuthentication(email);
        User user = getUser(email);

        Post post = Post.of(postWriteRequest.content(), user);
        postRepository.save(post);
        return PostWriteResponse.of(post);
    }

    private void checkAuthentication(String email) {
        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException("다시 로그인 부탁드립니다.");
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("다시 로그인 부탁드립니다.")
        );
    }

    public PostReadResponses findAll(Pageable pageable) {
        Page<Post> postPage = postRepository.findAll(pageable);

        return PostReadResponses.of(postPage);
    }

    public PostReadResponse findById(Long postId) {
        Post post = checkPostIdAndGetPost(postId);

        return PostReadResponse.of(post);
    }

    private Post checkPostIdAndGetPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(
                        () -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다. 다시한번 확인해주세요.")
                );
    }

    @Transactional
    public PostEditResponse editPost(HttpServletRequest httpServletRequest, Long postId, PostEditRequest postEditRequest) {
        Post post = checkPostIdAndGetPost(postId);

        String email = tokenProvider.getAuthentication(httpServletRequest).getName();
        checkAuthentication(email);
        User user = getUser(email);

        checkAuthorizationOfPost(post, user);

        post.update(postEditRequest);

        return PostEditResponse.of(post);
    }

    private void checkAuthorizationOfPost(Post post, User user) {
        if (!post.getUser().equals(user)) {
            throw new NoAuthorityException("해당 게시글에 대한 권한이 없습니다.");
        }
    }

    @Transactional
    public void deletePost(HttpServletRequest httpServletRequest, Long postId) {
        Post post = checkPostIdAndGetPost(postId);

        String email = tokenProvider.getAuthentication(httpServletRequest).getName();
        checkAuthentication(email);
        User user = getUser(email);

        checkAuthorizationOfPost(post, user);

        postRepository.delete(post);
    }
}
