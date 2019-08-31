package com.example.polly.PollyDemo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleInsertDTO {
    private String title;
    private Integer memberId;
    private String description;
    private LocalDateTime dueAt;
    private LocalDateTime remindAt;
    private String url;
}
