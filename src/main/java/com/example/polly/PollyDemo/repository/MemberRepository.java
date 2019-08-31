package com.example.polly.PollyDemo.repository;

import com.example.polly.PollyDemo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByUuid(String uuid);
}
