package com.example.polly.PollyDemo.controller;

import com.example.polly.PollyDemo.dto.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    @GetMapping("/members/me")
    public MemberResponse getMe(@RequestHeader(name = "Authorization", required = false) String accessToken) {
        return this.createMockMemberResponse();
    }

    private MemberResponse createMockMemberResponse() {
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setId(1);
        memberResponse.setUuid(UUID.randomUUID().toString());
        return memberResponse;
    }
}
