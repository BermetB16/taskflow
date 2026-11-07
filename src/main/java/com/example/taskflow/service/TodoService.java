package com.example.taskflow.service;

import com.example.taskflow.dto.*;
import com.example.taskflow.entity.HistoryEntry;
import com.example.taskflow.entity.Tag;
import com.example.taskflow.entity.Todo;
import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.Status;
import com.example.taskflow.exception.TodoNotFoundException;
import com.example.taskflow.repository.HistoryRepository;
import com.example.taskflow.repository.TagRepository;
import com.example.taskflow.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TodoService {

    private final TodoRepository todoRepo;
    private final TagRepository tagRepo;
    private final HistoryRepository historyRepo;

    public TodoService(TodoRepository todoRepo,
                       TagRepository tagRepo,
                       HistoryRepository historyRepo) {
        this.todoRepo = todoRepo;
        this.tagRepo = tagRepo;
        this.historyRepo = historyRepo;
    }

    public List<TodoResponse> list(Status status, Priority priority, String tag, String sort) {
        List<Todo> todos;
        if (tag != null && !tag.isBlank()) {
            todos = todoRepo.findByTagName(tag.toLowerCase());
        } else if (status != null) {
            todos = todoRepo.findByStatus(status);
        } else if (priority != null) {
            todos = todoRepo.findByPriority(priority);
        } else {
            todos = todoRepo.findAll();
        }

        Comparator<Todo> comparator = switch (sort == null ? "smart" : sort) {
            case "created" -> Comparator.comparing(Todo::getCreatedAt).reversed();
            case "deadline" -> Comparator.comparing(
                    Todo::getDeadline, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator
                    .comparing(Todo::getPriority).reversed()
                    .thenComparing(Todo::getDeadline,
                            Comparator.nullsLast(Comparator.naturalOrder()));
        };
        todos.sort(comparator);
        return todos.stream().map(this::toResponse).toList();
    }

    public TodoResponse get(Long id) {
        return toResponse(find(id));
    }

    public TodoResponse create(TodoRequest req) {
        Todo todo = new Todo();
        todo.setTitle(req.title());
        todo.setDescription(req.description());
        todo.setPriority(req.priority());
        todo.setStatus(req.status());
        todo.setDeadline(req.deadline());
        todo.setTags(resolveTags(req.tags()));
        Todo saved = todoRepo.save(todo);
        log(saved.getId(), "created", "Создана задача '" + saved.getTitle() + "'");
        return toResponse(saved);
    }

    public TodoResponse update(Long id, TodoUpdateRequest req) {
        Todo todo = find(id);
        if (req.title() != null) todo.setTitle(req.title());
        if (req.description() != null) todo.setDescription(req.description());
        if (req.priority() != null) todo.setPriority(req.priority());
        if (req.status() != null) todo.setStatus(req.status());
        if (req.deadline() != null) todo.setDeadline(req.deadline());
        if (req.tags() != null) todo.setTags(resolveTags(req.tags()));
        Todo saved = todoRepo.save(todo);
        log(id, "updated", "Обновлена задача");
        return toResponse(saved);
    }

    public TodoResponse complete(Long id) {
        Todo todo = find(id);
        todo.markDone();
        Todo saved = todoRepo.save(todo);
        log(id, "completed", "Задача выполнена");
        return toResponse(saved);
    }

    public void delete(Long id) {
        Todo todo = find(id);
        todoRepo.delete(todo);
    }

    public StatsResponse stats() {
        List<Todo> all = todoRepo.findAll();
        Map<String, Long> byStatus = new LinkedHashMap<>();
        for (Status s : Status.values()) byStatus.put(s.name(), 0L);
        Map<String, Long> byPriority = new LinkedHashMap<>();
        for (Priority p : Priority.values()) byPriority.put(p.name(), 0L);

        long overdue = 0, done = 0;
        for (Todo t : all) {
            byStatus.merge(t.getStatus().name(), 1L, Long::sum);
            byPriority.merge(t.getPriority().name(), 1L, Long::sum);
            if (t.isOverdue()) overdue++;
            if (t.getStatus() == Status.DONE) done++;
        }
        double rate = all.isEmpty() ? 0.0 : Math.round((done * 10000.0 / all.size())) / 100.0;
        return new StatsResponse(all.size(), byStatus, byPriority, overdue, rate);
    }

    public List<HistoryResponse> history(Long id) {
        find(id);
        return historyRepo.findByTodoIdOrderByCreatedAtAsc(id).stream()
                .map(h -> new HistoryResponse(h.getAction(), h.getDetails(), h.getCreatedAt()))
                .toList();
    }

    public List<TagResponse> allTags() {
        return tagRepo.findAll().stream()
                .map(t -> new TagResponse(t.getId(), t.getName()))
                .toList();
    }

    // ----- helpers -----

    private Todo find(Long id) {
        return todoRepo.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    }

    private Set<Tag> resolveTags(List<String> names) {
        if (names == null) return new HashSet<>();
        return names.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .map(name -> tagRepo.findByName(name).orElseGet(() -> tagRepo.save(new Tag(name))))
                .collect(Collectors.toSet());
    }

    private void log(Long todoId, String action, String details) {
        historyRepo.save(new HistoryEntry(todoId, action, details));
    }

    private TodoResponse toResponse(Todo t) {
        List<String> tagNames = t.getTags().stream()
                .map(Tag::getName)
                .sorted()
                .toList();
        return new TodoResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getPriority(),
                t.getStatus(),
                t.getDeadline(),
                t.getCreatedAt(),
                t.getUpdatedAt(),
                t.isOverdue(),
                tagNames
        );
    }
}