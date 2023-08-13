package com.wanted.internship.dto.post;

import com.wanted.internship.entity.Post;

public record PostReadResponse(
        Long postId,
        String content,
        Long userId
) {

    public static PostReadResponse of(Post post) {
        return new PostReadResponse(post.getId(), post.getContent(), post.getUser().getId());
    }
}
