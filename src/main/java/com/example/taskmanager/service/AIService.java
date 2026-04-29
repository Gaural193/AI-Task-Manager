package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public TaskResponse suggestTask(String prompt) {
        if ("mock_key_for_testing".equals(apiKey)) {
            // Return mock response for testing if no API key is provided
            return TaskResponse.builder()
                    .title("Mocked Task")
                    .description("Mocked from prompt: " + prompt)
                    .dueDate(LocalDate.now().plusDays(1))
                    .priority(Priority.MEDIUM)
                    .status(Status.TODO)
                    .build();
        }

        String systemInstruction = "You are a task management assistant. Given a prompt, extract task details and return ONLY a raw JSON object (without markdown wrappers like ```json) with the following structure: {\"title\": \"string\", \"description\": \"string\", \"dueDate\": \"YYYY-MM-DD\", \"priority\": \"LOW|MEDIUM|HIGH\", \"status\": \"TODO|IN_PROGRESS|DONE\"}. If a date is not specific, guess one in the future. Today is " + LocalDate.now().toString() + ".";
        
        String fullPrompt = systemInstruction + " Prompt: " + prompt;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                    Map.of("parts", List.of(
                            Map.of("text", fullPrompt)
                    ))
            ));

            String urlWithKey = apiUrl + "?key=" + apiKey;
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(urlWithKey, request, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String textResponse = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            
            // Clean markdown if the AI includes it despite instructions
            textResponse = textResponse.replace("```json", "").replace("```", "").trim();

            return objectMapper.readValue(textResponse, TaskResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate task from AI: " + e.getMessage(), e);
        }
    }
}
