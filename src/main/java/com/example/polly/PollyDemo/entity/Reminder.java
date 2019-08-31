package com.example.polly.PollyDemo.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "reminder_id")
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "schedule_id")
    private Integer scheduleId;

    @Column(name = "remind_at")
    private LocalDateTime remindAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
