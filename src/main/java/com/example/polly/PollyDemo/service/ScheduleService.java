package com.example.polly.PollyDemo.service;

import com.example.polly.PollyDemo.NotFoundException;
import com.example.polly.PollyDemo.entity.Schedule;
import com.example.polly.PollyDemo.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    @Transactional
    public Schedule createSchedule(String title) {
        Schedule schedule = Schedule.builder()
                .title(title)
                .build();
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public List<Schedule> getSchedules(Integer memberId, Pageable pageable) {
        if (memberId == null) {
            throw new IllegalArgumentException("'memberId' must not be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("'pageable' must not be null");
        }
        return scheduleRepository.findByMemberIdAndOrderByCreatedAt(memberId, pageable)
                .stream()
                .collect(Collectors.toList());
    }

    @Transactional
    public Schedule getSchedule(Integer memberId, Integer scheduleId) {
        if (memberId == null) {
            throw new IllegalArgumentException("'memberId' must not be null");
        }
        if (scheduleId == null) {
            throw new IllegalArgumentException("'scheduleId' must not be null");
        }
        return scheduleRepository.findById(scheduleId)
                .filter(schedule -> memberId.equals(schedule.getMemberId()))
                .orElseThrow(() -> new NotFoundException("schedule not found. scheduleId:" + scheduleId));
    }
}
