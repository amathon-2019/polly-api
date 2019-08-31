package com.example.polly.PollyDemo.controller;

import com.example.polly.PollyDemo.assembler.MemberAssembler;
import com.example.polly.PollyDemo.dto.MemberResponse;
import com.example.polly.PollyDemo.entity.Member;
import com.example.polly.PollyDemo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    private final MemberAssembler memberAssembler;

    @GetMapping("/members/me")
    public MemberResponse getMe(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                @ApiIgnore @RequestAttribute(name = "memberId") Integer memberId) {
        Member member = memberService.getMember(memberId);
        return memberAssembler.toMemberResponse(member);
    }
}
