package com.example.taskmanager.service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.exception.ResourceNotFoundException;
import com.example.taskmanager.model.Priority;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequest request;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Desc")
                .dueDate(LocalDate.now())
                .priority(Priority.HIGH)
                .status(Status.TODO)
                .build();

        request = TaskRequest.builder()
                .title("Test Task")
                .description("Test Desc")
                .dueDate(LocalDate.now())
                .priority(Priority.HIGH)
                .status(Status.TODO)
                .build();
    }

    @Test
    void createTask_ShouldReturnTaskResponse() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponse response = taskService.createTask(request);

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        assertEquals(Priority.HIGH, response.getPriority());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getAllTasks_ShouldReturnListOfTaskResponses() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));

        List<TaskResponse> responses = taskService.getAllTasks();

        assertEquals(1, responses.size());
        assertEquals("Test Task", responses.get(0).getTitle());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_WhenFound_ShouldReturnTaskResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_WhenNotFound_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.getTaskById(1L));
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void updateTask_ShouldReturnUpdatedTaskResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        request.setTitle("Updated Title");
        TaskResponse response = taskService.updateTask(1L, request);

        assertNotNull(response);
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldCallRepositoryDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).delete(task);
    }
}
