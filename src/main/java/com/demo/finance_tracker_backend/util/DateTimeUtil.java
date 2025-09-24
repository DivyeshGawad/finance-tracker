package com.demo.finance_tracker_backend.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTimeUtil {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Kolkata"); // 🇮🇳 IST (adjust as needed)

    public static LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, DEFAULT_ZONE) : null;
    }
}
