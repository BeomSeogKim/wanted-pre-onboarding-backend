package com.wanted.internship.dto.post;

public record PostDeleteResponse(
        String message
) {
    public static PostDeleteResponse of(String message) {
        return new PostDeleteResponse(message);
    }
}
