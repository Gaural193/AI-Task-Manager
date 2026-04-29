package com.example.taskmanager.dto;

import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDate dueDate;

    @NotNull(message = "Priority is required")
    private Priority priority;

    @NotNull(message = "Status is required")
    private Status status;
}
