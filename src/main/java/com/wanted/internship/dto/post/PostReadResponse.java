package com.wanted.internship.dto.post;

public record PostReadResponse(
        Long postId,
        String content,
        Long userId
) {
}
