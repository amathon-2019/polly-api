package com.example.polly.PollyDemo.service;

import com.example.polly.PollyDemo.NotFoundException;
import com.example.polly.PollyDemo.assembler.MemberAssembler;
import com.example.polly.PollyDemo.entity.Member;
import com.example.polly.PollyDemo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member createMember(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            throw new IllegalArgumentException("'uuid' must not be empty");
        }
        if (memberRepository.findByUuid(uuid).isPresent()) {
            throw new IllegalArgumentException("already used uuid");
        }
        Member member = Member.builder()
                .uuid(uuid)
                .build();
        return memberRepository.save(member);
    }

    @Transactional
    public Member getMember(Integer memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("'memberId' must not be null");
        }
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("member not found. memberId: " + memberId));
    }

}
