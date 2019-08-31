package com.example.polly.PollyDemo.service;

import com.example.polly.PollyDemo.entity.Reminder;
import com.example.polly.PollyDemo.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private final ReminderRepository reminderRepository;

    @Transactional
    public Reminder createReminder(Integer memberId, Integer scheduleId, LocalDateTime remindAt) {
        Reminder reminder = Reminder.builder()
                .userId(memberId)
                .scheduleId(scheduleId)
                .remindAt(remindAt)
                .build();
        return reminderRepository.save(reminder);
    }
}
