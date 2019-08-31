package com.example.polly.PollyDemo.service;

import com.example.polly.PollyDemo.component.JwtFactory;
import com.example.polly.PollyDemo.entity.Member;
import com.example.polly.PollyDemo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final JwtFactory jwtFactory;

    @Transactional
    public String login(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            throw new IllegalArgumentException("'uuid' must not be empty");
        }
        // uuid 로 멤버 조회. 없으면 새로 생성
        Member member = memberRepository.findByUuid(uuid)
                .orElse(memberService.createMember(uuid));
        return jwtFactory.generateToken(member.getId());
    }
}
