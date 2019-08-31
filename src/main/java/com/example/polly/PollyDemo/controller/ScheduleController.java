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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Map<String, List<ScheduleResponse>> getSchedules(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                               @ApiIgnore @RequestAttribute("memberId") Integer memberId,
                                               @RequestParam(defaultValue = "0") Integer page,
                                               @RequestParam(defaultValue = "20") Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        Map<String, List<ScheduleResponse>> map = new HashMap<>();
        map.put("responseData", scheduleService.getSchedules(memberId, pageable)
                .stream()
                .map(scheduleAssembler::toScheduleResponse)
                .collect(Collectors.toList()));

        return map;
    }

    @ApiOperation(value = "일정 한 개를 조회합니다.")
    @GetMapping("/schedules/{scheduleId}")
    public Map<String, ScheduleResponse> getSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                        @ApiIgnore @RequestAttribute("memberId") Integer memberId,
                                        @PathVariable Integer scheduleId) {

        Map<String, ScheduleResponse> map = new HashMap<>();
        Schedule schedule = scheduleService.getSchedule(memberId, scheduleId);
        map.put("responseData", scheduleAssembler.toScheduleResponse(schedule));
        return map;
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
                                           @ApiIgnore @RequestAttribute("memberId") Integer memberId,
                                           @RequestBody ScheduleRequest scheduleRequest) {

        log.info("]-----] POST :: ScheduleController.createSchedule [----[ : ScheduleRequest = {}", scheduleRequest);

        LocalDateTime localDateTime = LocalDateTime.now();
        String currentDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = currentDateTime + "_polly_sound.mp3";
        String text =
                scheduleRequest.getDueAt().getYear() + "년 " +
                        scheduleRequest.getDueAt().getMonthValue() + "월 " +
                        scheduleRequest.getDueAt().getDayOfMonth() + "일에 " +
                        scheduleRequest.getTitle() + ", " + scheduleRequest.getDescription() + " 이 있습니다.";

        String pollySoundUrl = pollyService.getMp3(text, fileName);

        ScheduleInsertDTO dto = new ScheduleInsertDTO();
        dto.setTitle(scheduleRequest.getTitle());
        dto.setMemberId(memberId);
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

    @ApiOperation(value = "일정을 삭제합니다.")
    @DeleteMapping("/schedules/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(@RequestHeader(name = "Authorization", required = false) String accessToken,
                               @ApiIgnore @RequestAttribute("memberId") Integer memberId,
                               @PathVariable Integer scheduleId) {
        scheduleService.deleteSchedule(memberId, scheduleId);
    }

    /**
     * 오늘 남은 일정을 브리핑하기
     */
    @ApiOperation(value = "오늘 남은 일정을 브리핑합니다. ")
    @PostMapping("/schedules/brief")
    public ScheduleBriefResponse berifSchedules(@RequestHeader(name = "Authorization", required = false) String accessToken,
                                                @ApiIgnore @RequestAttribute("memberId") Integer memberId) {
        log.info("]-----] POST :: ScheduleController.berifSchedules [----[ : accessToken = {}", accessToken);

        List<Schedule> items = scheduleService.getSchedulesForBriefing(memberId);

        LocalDateTime localDateTime = LocalDateTime.now();
        String currentDateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = currentDateTime + "_polly_sound.mp3";

        ScheduleBriefResponse scheduleBriefResponse = new ScheduleBriefResponse();

        if (items.size() < 1) {
            scheduleBriefResponse.setUrl(pollyService.getMp3("앞으로 남은 일정은 없습니다.", fileName));
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("오늘 예정된 일정은, ");
            for (Schedule item : items) {

                String AMorPM = "오전 ";
                String hour = item.getDueAt().getHour() + "시 ";
                String minute = item.getDueAt().getMinute() == 0 ? "" : item.getDueAt().getMinute() + "분 ";

                if (item.getDueAt().getHour() > 12) {
                    AMorPM = "오후 ";
                    hour = (item.getDueAt().getHour() - 12) + "시 ";
                }
                stringBuffer.append(AMorPM + hour + minute + item.getTitle() + ", ");
            }
            stringBuffer.append("입니다.");

            scheduleBriefResponse.setUrl(pollyService.getMp3(stringBuffer.toString(), fileName));
        }

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
