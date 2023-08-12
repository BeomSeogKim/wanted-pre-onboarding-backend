package com.wanted.internship.dto.user;

/*
이메일 조건 @ 포함
비밀번호 조건 : 8자 이상
 */
public record SignupRequest(
        String email,
        String password
) {
}
