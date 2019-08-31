package com.example.polly.PollyDemo.controller;

import com.example.polly.PollyDemo.dto.LoginRequest;
import com.example.polly.PollyDemo.dto.LoginResponse;
import com.example.polly.PollyDemo.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        String accessToken = loginService.login(loginRequest.getUuid());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        return loginResponse;
    }
}
