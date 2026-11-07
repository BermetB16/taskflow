package com.example.taskflow.dto;

import java.util.Map;

public record StatsResponse(
        long total,
        Map<String, Long> byStatus,
        Map<String, Long> byPriority,
        long overdue,
        double completionRate
) {
}