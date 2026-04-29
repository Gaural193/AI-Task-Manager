package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AIServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AIService aiService;

    @BeforeEach
    void setUp() {
        // We set a dummy key to bypass the mock logic in the method so we can test the real flow
        ReflectionTestUtils.setField(aiService, "apiKey", "dummy_key");
        ReflectionTestUtils.setField(aiService, "apiUrl", "http://dummy");
    }

    @Test
    void suggestTask_ShouldReturnParsedTaskResponse() throws Exception {
        String jsonResponse = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"{\\\"title\\\":\\\"Test\\\",\\\"description\\\":\\\"Desc\\\",\\\"priority\\\":\\\"HIGH\\\",\\\"status\\\":\\\"TODO\\\"}\"}]}}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        // We use a real ObjectMapper to parse the outer JSON for simplicity in mocking
        ObjectMapper realMapper = new ObjectMapper();
        when(objectMapper.readTree(jsonResponse)).thenReturn(realMapper.readTree(jsonResponse));
        
        TaskResponse expectedResponse = TaskResponse.builder()
                .title("Test")
                .description("Desc")
                .priority(Priority.HIGH)
                .status(Status.TODO)
                .build();
                
        when(objectMapper.readValue(anyString(), eq(TaskResponse.class))).thenReturn(expectedResponse);

        TaskResponse response = aiService.suggestTask("Do something high priority");

        assertNotNull(response);
        assertEquals("Test", response.getTitle());
        assertEquals(Priority.HIGH, response.getPriority());
    }
}
