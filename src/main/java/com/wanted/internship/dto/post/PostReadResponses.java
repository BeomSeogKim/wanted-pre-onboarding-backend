package com.wanted.internship.dto.post;

import com.wanted.internship.entity.Post;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public record PostReadResponses(
        List<PostReadResponse> postList,
        boolean firstPage,
        boolean lastPage,
        int currentPage,
        int totalPage
) {
    public static PostReadResponses of(Page<Post> postPage) {
        List<PostReadResponse> postReadResponseList = new ArrayList<>();
        boolean firstPage = postPage.isFirst();
        boolean lastPage = postPage.isLast();
        int currentPage = postPage.getNumber();
        int totalPages = postPage.getTotalPages();

        postPage.forEach(
                p -> postReadResponseList.add(
                        new PostReadResponse(p.getId(), p.getContent(), p.getUser().getId())
                )
        )
        ;
        return new PostReadResponses(postReadResponseList, firstPage, lastPage, currentPage, totalPages);
    }
}
