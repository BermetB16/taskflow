package com.example.taskflow.exception;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("Задача с id=" + id + " не найдена");
    }
}