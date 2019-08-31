package com.example.polly.PollyDemo.utils;

import java.time.LocalDateTime;

public final class DateTimeUtils {
    private DateTimeUtils() {
    }

    public static LocalDateTime getLastTimeOfDay(LocalDateTime localDateTime) {
        return LocalDateTime.of(
                localDateTime.getYear(),
                localDateTime.getMonth(),
                localDateTime.getDayOfMonth() + 1,
                0,
                0,
                0
        );
    }
}
