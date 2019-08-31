package com.example.polly.PollyDemo.controller;

import com.example.polly.PollyDemo.dto.LoginRequest;
import com.example.polly.PollyDemo.dto.LoginResponse;
import com.example.polly.PollyDemo.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "로그인", description = "인증이 필요하지 않은 요청입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {
    private final LoginService loginService;

    @ApiOperation(value = "uuid 를 입력하여 로그인을 요청합니다")
    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        String accessToken = loginService.login(loginRequest.getUuid());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        return loginResponse;
    }
}
