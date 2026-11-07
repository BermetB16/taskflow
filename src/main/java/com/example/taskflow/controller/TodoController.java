package com.example.taskflow.controller;

import com.example.taskflow.dto.*;
import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.Status;
import com.example.taskflow.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @GetMapping("/todos")
    public List<TodoResponse> list(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "smart") String sort
    ) {
        return service.list(status, priority, tag, sort);
    }

    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse create(@Valid @RequestBody TodoRequest req) {
        return service.create(req);
    }

    @GetMapping("/todos/{id}")
    public TodoResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PatchMapping("/todos/{id}")
    public TodoResponse update(@PathVariable Long id,
                               @RequestBody TodoUpdateRequest req) {
        return service.update(id, req);
    }

    @PostMapping("/todos/{id}/complete")
    public TodoResponse complete(@PathVariable Long id) {
        return service.complete(id);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/todos/{id}/history")
    public List<HistoryResponse> history(@PathVariable Long id) {
        return service.history(id);
    }

    @GetMapping("/stats")
    public StatsResponse stats() {
        return service.stats();
    }

    @GetMapping("/tags")
    public List<TagResponse> tags() {
        return service.allTags();
    }
}