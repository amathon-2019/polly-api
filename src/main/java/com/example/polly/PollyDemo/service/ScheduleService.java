package com.example.polly.PollyDemo.service;

import com.example.polly.PollyDemo.entity.Schedule;
import com.example.polly.PollyDemo.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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
}
