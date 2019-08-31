package com.example.polly.PollyDemo.controller;

import com.example.polly.PollyDemo.dto.LoginRequest;
import com.example.polly.PollyDemo.dto.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return this.createMockLoginResponse();
    }

    private LoginResponse createMockLoginResponse() {
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken("test");
        return loginResponse;
    }
}
