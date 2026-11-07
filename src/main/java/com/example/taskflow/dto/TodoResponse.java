package com.example.taskflow.dto;

import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public record TodoResponse(
        Long id,
        String title,
        String description,
        Priority priority,
        Status status,
        LocalDateTime deadline,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean isOverdue,
        List<String> tags
) {
}