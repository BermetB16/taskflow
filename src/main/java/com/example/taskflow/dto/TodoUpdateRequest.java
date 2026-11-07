package com.example.taskflow.dto;

import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public record TodoUpdateRequest(
        String title,
        String description,
        Priority priority,
        Status status,
        LocalDateTime deadline,
        List<String> tags
) {
}