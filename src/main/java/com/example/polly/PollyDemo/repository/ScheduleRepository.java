package com.example.polly.PollyDemo.repository;

import com.example.polly.PollyDemo.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    List<Schedule> findByMemberIdOrderByCreatedAt(Integer memberId, Pageable pageable);
    List<Schedule> findByMemberIdAndDueAtAfterAndDueAtBefore(Integer memberId, LocalDateTime now, LocalDateTime midnight);
}
