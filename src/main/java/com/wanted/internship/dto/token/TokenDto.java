package com.wanted.internship.dto.token;

public record TokenDto(
        String grantType,
        String accessToken,
        String refreshToken,
        Long accessTokenExpiresIn) {
}
