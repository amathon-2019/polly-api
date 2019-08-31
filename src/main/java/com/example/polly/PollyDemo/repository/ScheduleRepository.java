package com.example.polly.PollyDemo.repository;

import com.example.polly.PollyDemo.entity.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    Page<Schedule> findByMemberIdAndOrderByCreatedAt(Integer memberId, Pageable pageable);
}
