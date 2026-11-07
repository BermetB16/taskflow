package com.example.taskflow.dto;

import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record TodoRequest(
        @NotBlank @Size(max = 255) String title,
        String description,
        Priority priority,
        Status status,
        LocalDateTime deadline,
        List<String> tags
) {
    public TodoRequest {
        if (priority == null) priority = Priority.MEDIUM;
        if (status == null) status = Status.TODO;
        if (tags == null) tags = List.of();
    }
}