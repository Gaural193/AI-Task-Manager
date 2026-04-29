package com.example.taskmanager.controller;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void crudTaskOperations() throws Exception {
        // 1. Create a task
        TaskRequest request = TaskRequest.builder()
                .title("Integration Test Task")
                .description("Testing CRUD")
                .dueDate(LocalDate.now())
                .priority(Priority.LOW)
                .status(Status.TODO)
                .build();

        String responseStr = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Integration Test Task")))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(responseStr).get("id").asLong();

        // 2. Get the task
        mockMvc.perform(get("/tasks/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Integration Test Task")));

        // 3. Update the task
        request.setTitle("Updated Integration Test Task");
        request.setStatus(Status.IN_PROGRESS);

        mockMvc.perform(put("/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Integration Test Task")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

        // 4. Delete the task
        mockMvc.perform(delete("/tasks/" + id))
                .andExpect(status().isNoContent());

        // 5. Verify task is deleted
        mockMvc.perform(get("/tasks/" + id))
                .andExpect(status().isNotFound());
    }
}
