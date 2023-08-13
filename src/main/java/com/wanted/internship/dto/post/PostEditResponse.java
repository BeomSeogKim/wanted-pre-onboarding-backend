package com.wanted.internship.dto.post;

import com.wanted.internship.entity.Post;

public record PostEditResponse(
        Long postId,
        String content
) {
    public static PostEditResponse of(Post post) {
        return new PostEditResponse(post.getId(), post.getContent());
    }
}
