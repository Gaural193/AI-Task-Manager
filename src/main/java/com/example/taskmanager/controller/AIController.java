package com.example.taskmanager.controller;

import com.example.taskmanager.dto.SuggestRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.service.AIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/suggest")
    public ResponseEntity<TaskResponse> suggestTask(@Valid @RequestBody SuggestRequest request) {
        return ResponseEntity.ok(aiService.suggestTask(request.getPrompt()));
    }
}
