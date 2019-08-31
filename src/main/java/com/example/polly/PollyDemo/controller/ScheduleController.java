package com.example.polly.PollyDemo.controller;

import com.example.polly.PollyDemo.assembler.ScheduleAssembler;
import com.example.polly.PollyDemo.dto.ScheduleBriefResponse;
import com.example.polly.PollyDemo.dto.ScheduleInsertDTO;
import com.example.polly.PollyDemo.dto.ScheduleRequest;
import com.example.polly.PollyDemo.dto.ScheduleResponse;
import com.example.polly.PollyDemo.entity.Schedule;
import com.example.polly.PollyDemo.service.PollyService;
import com.example.polly.PollyDemo.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Api(value = "일정", description = "인증이 필요한 요청입니다.")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ScheduleController {

    private final PollyService pollyService;
    private final ScheduleService scheduleService;
    private final ScheduleAssembler scheduleAssembler;

    @ApiOperation(value = "일정 목록을 조회합니다.")
    @GetMapping("/schedules")
    public List<ScheduleResponse> getSchedules(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                               @ApiIgnore @RequestAttribute("memberId") Integer memberId,
                                               @RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "20") Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        return scheduleService.getSchedules(memberId, pageable)
                .stream()
                .map(scheduleAssembler::toScheduleResponse)
                .collect(Collectors.toList());
    }

    @ApiOperation(value = "일정 한 개를 조회합니다.")
    @GetMapping("/schedules/{scheduleId}")
    public ScheduleResponse getSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                        @ApiIgnore @RequestAttribute("memberId") Integer memberId,
                                        @PathVariable Integer scheduleId) {
        Schedule schedule = scheduleService.getSchedule(memberId, scheduleId);
        return scheduleAssembler.toScheduleResponse(schedule);
    }


    /**
     * 일정을 등록하는 컨트롤러
     *
     * @param accessToken
     * @param scheduleRequest
     */
    @ApiOperation(value = "일정을 등록합니다. ")
    @PostMapping("/schedules")
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse createSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                           @RequestBody ScheduleRequest scheduleRequest) {

        log.info("]-----] POST :: ScheduleController.createSchedule [----[ : ScheduleRequest = {}", scheduleRequest);

        LocalDateTime localDateTime = LocalDateTime.now();
        String currentDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = currentDateTime + "_polly_sound.mp3";
        String text =
                scheduleRequest.getDueAt().getYear()+"년 "+
                scheduleRequest.getDueAt().getMonthValue()+"월 "+
                scheduleRequest.getDueAt().getDayOfMonth()+"일에 "+
                scheduleRequest.getTitle()+", "+scheduleRequest.getDescription()+" 이 있습니다.";

        String pollySoundUrl = pollyService.getMp3(text, fileName);

        ScheduleInsertDTO dto = new ScheduleInsertDTO();
        dto.setTitle(scheduleRequest.getTitle());
        dto.setDescription(scheduleRequest.getDescription());
        dto.setDueAt(scheduleRequest.getDueAt());
        dto.setRemindAt(scheduleRequest.getRemindAt());
        dto.setUrl(pollySoundUrl);

        Schedule result = scheduleService.createSchedule(dto);

        ScheduleResponse scheduleResponse = scheduleAssembler.toScheduleResponse(result);

        return scheduleResponse;
    }

    @ApiOperation(value = "일정을 수정합니다. (아직 구현되지 않았습니다)")
    @PutMapping("/schedules/{scheduleId}")
    public ScheduleResponse updateSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                           @PathVariable Integer scheduleId,
                                           @RequestBody ScheduleRequest scheduleRequest) {
        return this.createScheduleResponse();
    }

    @ApiOperation(value = "일정을 삭제합니다. (아직 구현되지 않았습니다)")
    @DeleteMapping("/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                               @PathVariable Integer scheduleId) {

    }

    /**
     * 오늘 남은 일정을 브리핑하기
     */
    @ApiOperation(value = "오늘 남은 일정을 브리핑합니다. ")
    @PostMapping("/schedules/brief")
    public ScheduleBriefResponse berifSchedules(@RequestHeader(name = "Authorization", required = false) String accessToken) {
        ScheduleBriefResponse scheduleBriefResponse = new ScheduleBriefResponse();
        scheduleBriefResponse.setUrl("url");
        return scheduleBriefResponse;
    }

    private ScheduleResponse createScheduleResponse() {
        ScheduleResponse scheduleResponse = new ScheduleResponse();
        scheduleResponse.setId(1);
        scheduleResponse.setTitle("제목");
        scheduleResponse.setDescription("내용입니다");
        scheduleResponse.setUrl("url");
        return scheduleResponse;
    }
}
