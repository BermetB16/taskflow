package com.example.taskflow.repository;

import com.example.taskflow.entity.Todo;
import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByStatus(Status status);

    List<Todo> findByPriority(Priority priority);

    @Query("""
        SELECT DISTINCT t FROM Todo t
        JOIN t.tags tg
        WHERE tg.name = :tagName
    """)
    List<Todo> findByTagName(@Param("tagName") String tagName);
}