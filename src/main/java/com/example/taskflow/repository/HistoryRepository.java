package com.example.taskflow.repository;

import com.example.taskflow.entity.HistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<HistoryEntry, Long> {
    List<HistoryEntry> findByTodoIdOrderByCreatedAtAsc(Long todoId);
}