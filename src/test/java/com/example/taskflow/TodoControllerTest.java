package com.example.taskflow;

import com.example.taskflow.dto.TodoRequest;
import com.example.taskflow.enums.Priority;
import com.example.taskflow.enums.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createTodo_shouldReturn201() throws Exception {
        TodoRequest req = new TodoRequest(
                "API тест", "Проверка контроллера",
                Priority.URGENT, Status.TODO, null,
                List.of("api")
        );

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("API тест"))
                .andExpect(jsonPath("$.priority").value("URGENT"));
    }

    @Test
    void getTodo_shouldReturn404WhenNotExists() throws Exception {
        mockMvc.perform(get("/api/todos/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void stats_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists());
    }
}