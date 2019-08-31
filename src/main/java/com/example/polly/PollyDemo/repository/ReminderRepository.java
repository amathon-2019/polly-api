package com.example.polly.PollyDemo.repository;

import com.example.polly.PollyDemo.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, Integer> {
}
