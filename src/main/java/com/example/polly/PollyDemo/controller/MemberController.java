package com.example.polly.PollyDemo.controller;

import com.example.polly.PollyDemo.assembler.MemberAssembler;
import com.example.polly.PollyDemo.dto.MemberResponse;
import com.example.polly.PollyDemo.entity.Member;
import com.example.polly.PollyDemo.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "회원", description = "인증이 필요한 요청입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {
    private final MemberService memberService;
    private final MemberAssembler memberAssembler;

    @ApiOperation(value = "자기 자신의 정보를 조회합니다. ")
    @GetMapping("/members/me")
    public MemberResponse getMe(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                @ApiIgnore @RequestAttribute(name = "memberId") Integer memberId) {
        Member member = memberService.getMember(memberId);
        return memberAssembler.toMemberResponse(member);
    }
}
