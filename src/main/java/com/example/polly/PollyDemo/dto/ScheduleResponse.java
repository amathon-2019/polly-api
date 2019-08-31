package com.example.polly.PollyDemo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleResponse {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime remindAt;
    private String url;
}
