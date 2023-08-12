package com.wanted.internship.dto.post;

import com.wanted.internship.entity.Post;

public record PostWriteResponse(
        Long postId,
        String content,
        Long userId
) {
   public static PostWriteResponse of(Post post) {
       return new PostWriteResponse(post.getId(), post.getContent(), post.getUser().getId());
   }
}
