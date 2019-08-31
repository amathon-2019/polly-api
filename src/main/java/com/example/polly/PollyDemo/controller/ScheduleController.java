package com.example.polly.PollyDemo.controller;

import com.example.polly.PollyDemo.dto.ScheduleRequest;
import com.example.polly.PollyDemo.dto.ScheduleResponse;
import com.example.polly.PollyDemo.model.ResponseFileView;
import com.example.polly.PollyDemo.service.PollyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {

    private LocalDateTime localDateTime = LocalDateTime.now();
    private String currentDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    private String fileName = currentDateTime+"_polly_sound.mp3";

    @Autowired
    private PollyService pollyService;

    @GetMapping("/schedules")
    public List<ScheduleResponse> getSchedules(@RequestHeader(name = "Authorization", required = false) String accessToken) {
        return Collections.singletonList(this.createScheduleResponse());
    }

    @GetMapping("/schedules/{scheduleId}")
    public ScheduleResponse getSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                        @PathVariable Integer scheduleId) {
        return this.createScheduleResponse();
    }


    /**
     * 일정을 등록하는 컨트롤러
     *
     * @param accessToken
     * @param scheduleRequest
     * */
    @PostMapping("/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse createSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                           @RequestBody ScheduleRequest scheduleRequest) {

        log.info("]-----] POST :: ScheduleController.createSchedule [----[ : ScheduleRequest = {}", scheduleRequest);

        /*
        1. 할일 하나에 대한 미리알림 시 제목, 본문 같이 읽기
        ex) 00시00분에 {제목}, {본문} 이 있습니다.

        2. briefing
        - 버튼 누를 시 -> 현재부터 24:00까지 해야할 모든 일정을 브리핑
        ex) 앞으로 오늘 남은 일정은 00시00분에 {제목}, 00시00분에 {제목}, 00시00분에 {제목} 입니다.
        ex) 앞으로 남은 일정은 없습니다.
        - 알림 시각을 설정하여 브리핑
        ex) 오늘 예정된 일정은 ({시간}, {제목}), ({시간} {제목}), ({시간} {제목}) 입니다.
        ex) 앞으로 남은 일정은 없습니다. (edited)
        * */

        ScheduleResponse response = new ScheduleResponse();
        ScheduleResponse scheduleResponse = new ScheduleResponse();
        scheduleResponse.setId(1);
        scheduleResponse.setTitle(scheduleRequest.getTitle());
        scheduleResponse.setDescription(scheduleRequest.getDescription());
        scheduleResponse.setRemindAt(localDateTime);
        scheduleResponse.setUrl(pollyService.getMp3(scheduleRequest.getDescription(), fileName));

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
