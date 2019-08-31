package com.example.polly.PollyDemo.assembler;

import com.example.polly.PollyDemo.dto.ScheduleResponse;
import com.example.polly.PollyDemo.entity.Schedule;
import org.springframework.stereotype.Component;

@Component
public class ScheduleAssembler {
    public ScheduleResponse toScheduleResponse(Schedule schedule) {
        if (schedule == null) {
            return null;
        }
        ScheduleResponse scheduleResponse = new ScheduleResponse();
        scheduleResponse.setId(schedule.getId());
        scheduleResponse.setUrl(schedule.getUrl());
        scheduleResponse.setDueAt(schedule.getDueAt());
        scheduleResponse.setTitle(schedule.getTitle());
        scheduleResponse.setDescription(schedule.getDescription());
        return scheduleResponse;
    }

}
