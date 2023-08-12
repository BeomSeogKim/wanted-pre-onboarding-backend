package com.wanted.internship.controller;

import com.wanted.internship.dto.user.SignupRequest;
import com.wanted.internship.dto.user.SignupResponse;
import com.wanted.internship.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Value("${url.host}")
    private String url;

    @PostMapping
    public ResponseEntity<SignupResponse> signUp(@RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = userService.signUp(signupRequest);
        return ResponseEntity.created(URI.create(url)).body(signupResponse);
    }
}
