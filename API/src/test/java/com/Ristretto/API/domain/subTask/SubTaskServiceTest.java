package com.Ristretto.API.domain.subTask;

import com.Ristretto.API.domain.task.Task;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubTaskServiceTest {

    @Mock
    private SubTaskRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private SubTaskService subTaskService;

    private Task mockTask;
    private SubTask mockSubTask;
    private SubTaskRegistrationData completeSubTaskData;
    private SubTaskRegistrationData minimalSubTaskData;
    private final Long subTaskId = 1L;
    private final Long taskId = 1L;

    @BeforeEach
    void setUp() {
        mockTask = new Task();
        mockSubTask = new SubTask();

        ReflectionTestUtils.setField(subTaskService, "entityManager", entityManager);
        lenient().when(entityManager.getReference(Task.class, taskId)).thenReturn(mockTask);

        completeSubTaskData = new SubTaskRegistrationData(
                "Complete SubTask",
                false,
                "Detailed description of the subtask",
                LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                taskId
        );

        minimalSubTaskData = new SubTaskRegistrationData(
                "Minimal SubTask",
                null,
                null,
                null,
                taskId
        );
    }

    @Test
    void findByTaskId_ShouldReturnPageOfSubTasks_WhenTaskIdProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SubTask> expectedPage = new PageImpl<>(Arrays.asList(mockSubTask));
        when(repository.findByTaskId(taskId, pageable)).thenReturn(expectedPage);

        Page<SubTask> result = subTaskService.findByTaskId(pageable, taskId);

        assertEquals(expectedPage, result);
        verify(repository, times(1)).findByTaskId(taskId, pageable);
    }

    @Test
    void convertToEntity_ShouldCreateCompleteSubTask_WhenCompleteDataProvided() {
        SubTask result = subTaskService.convertToEntity(completeSubTaskData);

        assertEquals(completeSubTaskData.title(), result.getTitle());
        assertEquals(completeSubTaskData.isDone(), result.getIsDone());
        assertEquals(completeSubTaskData.description(), result.getDescription());
        assertEquals(completeSubTaskData.dueDate(), result.getDueDate());
        assertEquals(mockTask, result.getTask());

        verify(entityManager, times(1)).getReference(Task.class, taskId);
    }

    @Test
    void convertToEntity_ShouldCreateMinimalSubTask_WhenMinimalDataProvided() {
        SubTask result = subTaskService.convertToEntity(minimalSubTaskData);

        assertEquals(minimalSubTaskData.title(), result.getTitle());
        assertEquals(minimalSubTaskData.isDone(), result.getIsDone());
        assertEquals(minimalSubTaskData.description(), result.getDescription());
        assertEquals(minimalSubTaskData.dueDate(), result.getDueDate());
        assertEquals(mockTask, result.getTask());

        verify(entityManager, times(1)).getReference(Task.class, taskId);
    }

    @Test
    void save_ShouldReturnSavedSubTask_WhenSubTaskProvided() {
        when(repository.save(mockSubTask)).thenReturn(mockSubTask);

        SubTask result = subTaskService.save(mockSubTask);

        assertEquals(mockSubTask, result);
        verify(repository, times(1)).save(mockSubTask);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete_WhenIdProvided() {
        subTaskService.deleteById(subTaskId);

        verify(repository, times(1)).deleteById(subTaskId);
    }
}