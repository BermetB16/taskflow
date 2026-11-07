package com.example.taskflow.dto;

import java.time.LocalDateTime;

public record HistoryResponse(String action, String details, LocalDateTime at) {
}