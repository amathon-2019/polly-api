package com.example.polly.PollyDemo.assembler;

import com.example.polly.PollyDemo.dto.MemberResponse;
import com.example.polly.PollyDemo.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAssembler {

    public MemberResponse toMemberResponse(Member member) {
        if (member == null) {
            return null;
        }
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setId(member.getId());
        memberResponse.setUuid(member.getUuid());
        return memberResponse;
    }
}
