package com.wanted.internship.service;

import com.wanted.internship.dto.post.PostReadResponse;
import com.wanted.internship.dto.post.PostReadResponses;
import com.wanted.internship.dto.post.PostWriteRequest;
import com.wanted.internship.dto.post.PostWriteResponse;
import com.wanted.internship.entity.Post;
import com.wanted.internship.entity.User;
import com.wanted.internship.repository.PostRepository;
import com.wanted.internship.repository.UserRepository;
import com.wanted.internship.security.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        String email = tokenProvider.getAuthentication(httpServletRequest).getName();
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
}
