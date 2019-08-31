package com.example.polly.PollyDemo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleRequest {
    private String title;
    private String description;
    private LocalDateTime dueAt;
    private LocalDateTime remindAt;
}
