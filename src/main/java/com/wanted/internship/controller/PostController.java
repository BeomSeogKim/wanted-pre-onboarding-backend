package com.wanted.internship.controller;

import com.wanted.internship.dto.post.*;
import com.wanted.internship.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    @Value("${url.host}")
    private String url;

    @PostMapping
    public ResponseEntity<PostWriteResponse> write(
            HttpServletRequest httpServletRequest,
            @RequestBody @Valid PostWriteRequest postWriteRequest) {
        PostWriteResponse postWriteResponse = postService.write(httpServletRequest, postWriteRequest);
        return ResponseEntity.created(URI.create(url)).body(postWriteResponse);
    }

    @GetMapping
    public ResponseEntity<PostReadResponses> findAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        PostReadResponses postReadResponses = postService.findAll(pageable);
        return ResponseEntity.ok().body(postReadResponses);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostReadResponse> findById(
            @PathVariable Long postId
    ) {
        PostReadResponse postReadResponse = postService.findById(postId);
        return ResponseEntity.ok().body(postReadResponse);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostEditResponse> editPost(
            HttpServletRequest httpServletRequest,
            @PathVariable Long postId,
            @RequestBody @Valid PostEditRequest postEditRequest
    ) {
        PostEditResponse postEditResponse = postService.editPost(httpServletRequest, postId, postEditRequest);
        return ResponseEntity.ok().body(postEditResponse);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDeleteResponse> deletePost(
            HttpServletRequest httpServletRequest,
            @PathVariable Long postId
    ) {
        postService.deletePost(httpServletRequest, postId);
        return ResponseEntity.ok().body(PostDeleteResponse.of("성공적으로 삭제를 완료했습니다."));
    }
}
