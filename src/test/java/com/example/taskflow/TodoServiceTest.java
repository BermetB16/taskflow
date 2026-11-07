package com.example.taskflow;

import com.example.taskflow.dto.*;
import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.Status;
import com.example.taskflow.exception.TodoNotFoundException;
import com.example.taskflow.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TodoServiceTest {

    @Autowired
    private TodoService service;

    @Test
    void createTodo_shouldReturnSavedTodo() {
        TodoRequest req = new TodoRequest(
                "Тестовая задача", "Описание",
                Priority.HIGH, Status.TODO, null,
                List.of("test")
        );

        TodoResponse response = service.create(req);

        assertNotNull(response.id());
        assertEquals("Тестовая задача", response.title());
        assertEquals(Priority.HIGH, response.priority());
        assertEquals(Status.TODO, response.status());
        assertTrue(response.tags().contains("test"));
    }

    @Test
    void getTodo_shouldThrowWhenNotFound() {
        assertThrows(TodoNotFoundException.class, () -> service.get(9999L));
    }

    @Test
    void complete_shouldChangeStatusToDone() {
        TodoRequest req = new TodoRequest(
                "Завершить меня", null,
                Priority.MEDIUM, Status.TODO, null,
                List.of()
        );
        TodoResponse created = service.create(req);

        TodoResponse completed = service.complete(created.id());

        assertEquals(Status.DONE, completed.status());
    }

    @Test
    void list_shouldFilterByStatus() {
        service.create(new TodoRequest("A", null, Priority.LOW, Status.TODO, null, List.of()));
        service.create(new TodoRequest("B", null, Priority.LOW, Status.TODO, null, List.of()));
        TodoResponse c = service.create(
                new TodoRequest("C", null, Priority.LOW, Status.TODO, null, List.of()));
        service.complete(c.id());

        List<TodoResponse> done = service.list(Status.DONE, null, null, "smart");

        assertEquals(1, done.size());
        assertEquals("C", done.get(0).title());
    }

    @Test
    void stats_shouldCountCorrectly() {
        service.create(new TodoRequest("A", null, Priority.HIGH, Status.TODO, null, List.of()));
        TodoResponse b = service.create(
                new TodoRequest("B", null, Priority.LOW, Status.TODO, null, List.of()));
        service.complete(b.id());

        StatsResponse stats = service.stats();

        assertEquals(2, stats.total());
        assertEquals(1L, stats.byStatus().get("DONE"));
        assertEquals(1L, stats.byStatus().get("TODO"));
        assertEquals(50.0, stats.completionRate());
    }
}