package com.example.polly.PollyDemo.controller;

import com.example.polly.PollyDemo.dto.ScheduleRequest;
import com.example.polly.PollyDemo.dto.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {
    @GetMapping("/schedules")
    public List<ScheduleResponse> getSchedules(@RequestHeader(name = "Authorization", required = false) String accessToken) {
        return Collections.singletonList(this.createScheduleResponse());
    }

    @GetMapping("/schedules/{scheduleId}")
    public ScheduleResponse getSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                        @PathVariable Integer scheduleId) {
        return this.createScheduleResponse();
    }

    @PostMapping("/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse createSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                           @RequestBody ScheduleRequest scheduleRequest) {
        return this.createScheduleResponse();
    }

    @PutMapping("/schedules/{scheduleId}")
    public ScheduleResponse updateSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                           @PathVariable Integer scheduleId,
                                           @RequestBody ScheduleRequest scheduleRequest) {
        return this.createScheduleResponse();
    }

    @DeleteMapping("/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                               @PathVariable Integer scheduleId) {

    }

    private ScheduleResponse createScheduleResponse() {
        ScheduleResponse scheduleResponse = new ScheduleResponse();
        scheduleResponse.setId(1);
        scheduleResponse.setTitle("제목");
        scheduleResponse.setDescription("내용입니다");
        scheduleResponse.setRemindAt(LocalDateTime.now());
        scheduleResponse.setUrl("url");
        return scheduleResponse;
    }
}
