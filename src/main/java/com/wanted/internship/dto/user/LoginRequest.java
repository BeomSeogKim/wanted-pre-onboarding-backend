package com.wanted.internship.dto.user;

public record LoginRequest(
        String email,
        String password
) {
}
