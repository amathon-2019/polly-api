package com.example.polly.PollyDemo.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.example.polly.PollyDemo.NotFoundException;
import com.example.polly.PollyDemo.dto.ScheduleInsertDTO;
import com.example.polly.PollyDemo.entity.Schedule;
import com.example.polly.PollyDemo.repository.ScheduleRepository;
import com.example.polly.PollyDemo.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private static final Pattern PATTERN = Pattern.compile("http://.+?/(.*)");

    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AmazonS3Client s3Client;

    @Transactional
    public Schedule createSchedule(String title) {
        Schedule schedule = Schedule.builder()
                .title(title)
                .build();
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public Schedule createSchedule(ScheduleInsertDTO dto) {
        Schedule schedule = Schedule.builder()
                .title(dto.getTitle())
                .memberId(dto.getMemberId())
                .description(dto.getDescription())
                .dueAt(dto.getDueAt())
                .remindAt(dto.getRemindAt())
                .url(dto.getUrl())
                .build();
        return scheduleRepository.save(schedule);
    }

    @Transactional
    public List<Schedule> getSchedules(Integer memberId, Pageable pageable) {
        if (memberId == null) {
            throw new IllegalArgumentException("'memberId' must not be null");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("'pageable' must not be null");
        }
        return scheduleRepository.findByMemberIdOrderByCreatedAt(memberId, pageable);
    }

    @Transactional
    public Schedule getSchedule(Integer memberId, Integer scheduleId) {
        if (memberId == null) {
            throw new IllegalArgumentException("'memberId' must not be null");
        }
        if (scheduleId == null) {
            throw new IllegalArgumentException("'scheduleId' must not be null");
        }
        return scheduleRepository.findById(scheduleId)
                .filter(schedule -> memberId.equals(schedule.getMemberId()))
                .orElseThrow(() -> new NotFoundException("schedule not found. scheduleId:" + scheduleId));
    }

    @Transactional
    public List<Schedule> getSchedulesForBriefing(Integer memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("'memberId' must not be null");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = DateTimeUtils.getLastTimeOfDay(now);
        return scheduleRepository.findByMemberIdAndDueAtAfterAndDueAtBefore(memberId, now, midnight);
    }

    @Transactional
    public void deleteSchedule(Integer memberId, Integer scheduleId) {
        if (memberId == null) {
            throw new IllegalArgumentException("'memberId' must not be null");
        }
        if (scheduleId == null) {
            throw new IllegalArgumentException("'scheduleId' must not be null");
        }
        scheduleRepository.findById(scheduleId)
                .map(schedule -> {
                    String url = schedule.getUrl();
                    Matcher matcher = PATTERN.matcher(url);
                    if (!matcher.matches()) {
                        return schedule;
                    }
                    String fileName = matcher.group(1);
                    if (StringUtils.isEmpty(fileName)) {
                        return schedule;
                    }
                    String bucketName = s3Client.listBuckets()
                            .stream()
                            .map(Bucket::getName)
                            .findFirst()
                            .orElse(null);
                    applicationEventPublisher.publishEvent(ScheduleDeletedEvent.of(bucketName, fileName));
                    return schedule;
                })
                .filter(schedule -> memberId.equals(schedule.getMemberId()))
                .ifPresent(scheduleRepository::delete);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteBucketObject(ScheduleDeletedEvent event) {
        s3Client.deleteBucketAnalyticsConfiguration(
                event.getBucketName(),
                event.getObjectId()
        );
    }

    @Value(staticConstructor = "of")
    public static class ScheduleDeletedEvent {
        private final String bucketName;
        private final String objectId;
    }
}
