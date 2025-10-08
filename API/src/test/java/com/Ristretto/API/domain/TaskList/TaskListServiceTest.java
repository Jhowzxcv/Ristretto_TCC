package com.Ristretto.API.domain.taskList;

import com.Ristretto.API.domain.user.User;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskListServiceTest {

    @Mock
    private TaskListRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TaskListService taskListService;

    private User mockUser;
    private TaskList mockTaskList;
    private TaskListRegistrationData cyclicTaskListData;
    private TaskListRegistrationData standardTaskListData;
    private final Long taskListId = 1L;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockTaskList = new TaskList();

        ReflectionTestUtils.setField(taskListService, "entityManager", entityManager);
        lenient().when(entityManager.getReference(User.class, userId)).thenReturn(mockUser);

        cyclicTaskListData = new TaskListRegistrationData(
                "Cyclic Task List",
                "#FF5733",
                "cycle-icon",
                ListType.CYCLIC,
                7,
                IntervalType.DAYS,
                true,
                false,
                null,
                userId
        );

        standardTaskListData = new TaskListRegistrationData(
                "Standard Task List",
                "#33FF57",
                "list-icon",
                ListType.STANDARD,
                null,
                null,
                null,
                null,
                LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                userId
        );
    }

    @Test
    void findAll_ShouldReturnPageOfTaskLists_WhenCalled() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaskList> expectedPage = new PageImpl<>(Arrays.asList(mockTaskList));
        when(repository.findAll(pageable)).thenReturn(expectedPage);

        Page<TaskList> result = taskListService.findAll(pageable);

        assertEquals(expectedPage, result);
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void findByid_ShouldReturnTaskList_WhenTaskListExists() {
        when(repository.findById(taskListId)).thenReturn(Optional.of(mockTaskList));

        TaskList result = taskListService.findByid(taskListId);

        assertEquals(mockTaskList, result);
        verify(repository, times(1)).findById(taskListId);
    }

    @Test
    void findByid_ShouldThrowException_WhenTaskListNotFound() {
        when(repository.findById(taskListId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> taskListService.findByid(taskListId));

        assertEquals("TaskList not found with id: " + taskListId, exception.getMessage());
        verify(repository, times(1)).findById(taskListId);
    }

    @Test
    void convertToEntity_ShouldCreateCyclicTaskList_WhenCyclicDataProvided() {
        TaskList result = taskListService.convertToEntity(cyclicTaskListData);

        assertEquals(cyclicTaskListData.title(), result.getTitle());
        assertEquals(cyclicTaskListData.color(), result.getColor());
        assertEquals(cyclicTaskListData.icon(), result.getIcon());
        assertEquals(cyclicTaskListData.type(), result.getType());
        assertEquals(cyclicTaskListData.repetitionIntervalAmount(), result.getRepetitionIntervalAmount());
        assertEquals(cyclicTaskListData.repetitionIntervalType(), result.getRepetitionIntervalType());
        assertEquals(cyclicTaskListData.repeatWhenComplete(), result.getRepeatWhenComplete());
        assertEquals(cyclicTaskListData.manualRestartAllowed(), result.getManualRestartAllowed());
        assertEquals(cyclicTaskListData.deadline(), result.getDeadline());
        assertEquals(mockUser, result.getUser());

        verify(entityManager, times(1)).getReference(User.class, userId);
    }

    @Test
    void convertToEntity_ShouldCreateStandardTaskList_WhenStandardDataProvided() {
        TaskList result = taskListService.convertToEntity(standardTaskListData);

        assertEquals(standardTaskListData.title(), result.getTitle());
        assertEquals(standardTaskListData.color(), result.getColor());
        assertEquals(standardTaskListData.icon(), result.getIcon());
        assertEquals(standardTaskListData.type(), result.getType());
        assertEquals(standardTaskListData.repetitionIntervalAmount(), result.getRepetitionIntervalAmount());
        assertEquals(standardTaskListData.repetitionIntervalType(), result.getRepetitionIntervalType());
        assertEquals(standardTaskListData.repeatWhenComplete(), result.getRepeatWhenComplete());
        assertEquals(standardTaskListData.manualRestartAllowed(), result.getManualRestartAllowed());
        assertEquals(standardTaskListData.deadline(), result.getDeadline());
        assertEquals(mockUser, result.getUser());

        verify(entityManager, times(1)).getReference(User.class, userId);
    }

    @Test
    void save_ShouldReturnSavedTaskList_WhenTaskListProvided() {
        when(repository.save(mockTaskList)).thenReturn(mockTaskList);

        TaskList result = taskListService.save(mockTaskList);

        assertEquals(mockTaskList, result);
        verify(repository, times(1)).save(mockTaskList);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete_WhenIdProvided() {
        taskListService.deleteById(taskListId);

        verify(repository, times(1)).deleteById(taskListId);
    }
}