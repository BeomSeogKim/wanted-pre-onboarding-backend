package com.wanted.internship.controller;

import com.wanted.internship.dto.post.PostWriteRequest;
import com.wanted.internship.dto.post.PostWriteResponse;
import com.wanted.internship.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
