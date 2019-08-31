package com.example.polly.PollyDemo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScheduleResponse {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime dueAt;
    private String url;
}
