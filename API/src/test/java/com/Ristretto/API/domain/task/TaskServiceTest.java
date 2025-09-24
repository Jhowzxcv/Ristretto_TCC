package com.Ristretto.API.domain.task;

import com.Ristretto.API.domain.category.Category;
import com.Ristretto.API.domain.goal.Goal;
import com.Ristretto.API.domain.taskList.TaskList;
import com.Ristretto.API.domain.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TaskService taskService;

    private User mockUser;
    private Category mockCategory;
    private Goal mockGoal;
    private TaskList mockTaskList;
    private Task mockTask;
    private TaskRegistrationData completeTaskData;
    private TaskRegistrationData taskDataWithoutTaskList;
    private TaskRegistrationData taskDataWithoutGoalAndTaskList;
    private TaskRegistrationData taskDataWithoutCategoryGoalAndTaskList;
    private final Long taskId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockCategory = new Category();
        mockGoal = new Goal();
        mockTaskList = new TaskList();
        mockTask = new Task();

        ReflectionTestUtils.setField(taskService, "entityManager", entityManager);
        lenient().when(entityManager.getReference(User.class, 1L)).thenReturn(mockUser);
        lenient().when(entityManager.getReference(Category.class, 2L)).thenReturn(mockCategory);
        lenient().when(entityManager.getReference(Goal.class, 3L)).thenReturn(mockGoal);
        lenient().when(entityManager.getReference(TaskList.class, 4L)).thenReturn(mockTaskList);

        completeTaskData = new TaskRegistrationData(
                "Complete Task",
                "Task with all associations",
                LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                Priority.MEDIUM,
                Status.NOT_STARTED,
                1L,
                2L,
                3L,
                4L,
                true,
                false
        );

        taskDataWithoutTaskList = new TaskRegistrationData(
                "Task Without List",
                "Task without task list association",
                LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                Priority.MEDIUM,
                Status.NOT_STARTED,
                1L,
                2L,
                3L,
                null,
                true,
                false
        );

        taskDataWithoutGoalAndTaskList = new TaskRegistrationData(
                "Task Without Goal and List",
                "Task without goal and task list associations",
                LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                Priority.MEDIUM,
                Status.NOT_STARTED,
                1L,
                2L,
                null,
                null,
                true,
                false
        );

        taskDataWithoutCategoryGoalAndTaskList = new TaskRegistrationData(
                "Minimal Task",
                "Task with only user association",
                LocalDateTime.of(2025, 12, 31, 23, 59, 59),
                Priority.MEDIUM,
                Status.NOT_STARTED,
                1L,
                null,
                null,
                null,
                true,
                false
        );
    }

    @Test
    void convertToEntity_ShouldCreateTaskWithAllAssociations_WhenAllIdsProvided() {
        Task result = taskService.convertToEntity(completeTaskData);

        assertEquals(completeTaskData.title(), result.getTitle());
        assertEquals(completeTaskData.description(), result.getDescription());
        assertEquals(completeTaskData.dueDate(), result.getDueDate());
        assertEquals(completeTaskData.priority(), result.getPriority());
        assertEquals(completeTaskData.status(), result.getStatus());
        assertEquals(completeTaskData.focusMode(), result.getFocusMode());
        assertEquals(completeTaskData.isDone(), result.getIsDone());

        assertEquals(mockUser, result.getUser());
        assertEquals(mockCategory, result.getCategory());
        assertEquals(mockGoal, result.getGoal());
        assertEquals(mockTaskList, result.getTaskList());

        verify(entityManager, times(1)).getReference(User.class, 1L);
        verify(entityManager, times(1)).getReference(Category.class, 2L);
        verify(entityManager, times(1)).getReference(Goal.class, 3L);
        verify(entityManager, times(1)).getReference(TaskList.class, 4L);
    }

    @Test
    void convertToEntity_ShouldCreateTaskWithoutTaskList_WhenTaskListIdIsNull() {
        Task result = taskService.convertToEntity(taskDataWithoutTaskList);

        assertEquals(taskDataWithoutTaskList.title(), result.getTitle());
        assertEquals(taskDataWithoutTaskList.description(), result.getDescription());
        assertEquals(taskDataWithoutTaskList.dueDate(), result.getDueDate());
        assertEquals(taskDataWithoutTaskList.priority(), result.getPriority());
        assertEquals(taskDataWithoutTaskList.status(), result.getStatus());
        assertEquals(taskDataWithoutTaskList.focusMode(), result.getFocusMode());
        assertEquals(taskDataWithoutTaskList.isDone(), result.getIsDone());

        assertEquals(mockUser, result.getUser());
        assertEquals(mockCategory, result.getCategory());
        assertEquals(mockGoal, result.getGoal());
        assertNull(result.getTaskList());

        verify(entityManager, times(1)).getReference(User.class, 1L);
        verify(entityManager, times(1)).getReference(Category.class, 2L);
        verify(entityManager, times(1)).getReference(Goal.class, 3L);
        verify(entityManager, never()).getReference(TaskList.class, any());
    }

    @Test
    void convertToEntity_ShouldCreateTaskWithoutGoalAndTaskList_WhenBothIdsAreNull() {
        Task result = taskService.convertToEntity(taskDataWithoutGoalAndTaskList);

        assertEquals(taskDataWithoutGoalAndTaskList.title(), result.getTitle());
        assertEquals(taskDataWithoutGoalAndTaskList.description(), result.getDescription());
        assertEquals(taskDataWithoutGoalAndTaskList.dueDate(), result.getDueDate());
        assertEquals(taskDataWithoutGoalAndTaskList.priority(), result.getPriority());
        assertEquals(taskDataWithoutGoalAndTaskList.status(), result.getStatus());
        assertEquals(taskDataWithoutGoalAndTaskList.focusMode(), result.getFocusMode());
        assertEquals(taskDataWithoutGoalAndTaskList.isDone(), result.getIsDone());

        assertEquals(mockUser, result.getUser());
        assertEquals(mockCategory, result.getCategory());
        assertNull(result.getGoal());
        assertNull(result.getTaskList());

        verify(entityManager, times(1)).getReference(User.class, 1L);
        verify(entityManager, times(1)).getReference(Category.class, 2L);
        verify(entityManager, never()).getReference(Goal.class, any());
        verify(entityManager, never()).getReference(TaskList.class, any());
    }

    @Test
    void findAll_ShouldReturnPageOfTasks_WhenCalled() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Task> expectedPage = new PageImpl<>(Arrays.asList(mockTask));
        when(repository.findAll(pageable)).thenReturn(expectedPage);

        Page<Task> result = taskService.findAll(pageable);

        assertEquals(expectedPage, result);
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    void save_ShouldReturnSavedTask_WhenTaskProvided() {
        when(repository.save(mockTask)).thenReturn(mockTask);

        Task result = taskService.save(mockTask);

        assertEquals(mockTask, result);
        verify(repository, times(1)).save(mockTask);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete_WhenIdProvided() {
        taskService.deleteById(taskId);

        verify(repository, times(1)).deleteById(taskId);
    }

    @Test
    void convertToEntity_ShouldCreateMinimalTask_WhenOnlyUserIdProvided() {
        Task result = taskService.convertToEntity(taskDataWithoutCategoryGoalAndTaskList);

        assertEquals(taskDataWithoutCategoryGoalAndTaskList.title(), result.getTitle());
        assertEquals(taskDataWithoutCategoryGoalAndTaskList.description(), result.getDescription());
        assertEquals(taskDataWithoutCategoryGoalAndTaskList.dueDate(), result.getDueDate());
        assertEquals(taskDataWithoutCategoryGoalAndTaskList.priority(), result.getPriority());
        assertEquals(taskDataWithoutCategoryGoalAndTaskList.status(), result.getStatus());
        assertEquals(taskDataWithoutCategoryGoalAndTaskList.focusMode(), result.getFocusMode());
        assertEquals(taskDataWithoutCategoryGoalAndTaskList.isDone(), result.getIsDone());

        assertEquals(mockUser, result.getUser());
        assertNull(result.getCategory());
        assertNull(result.getGoal());
        assertNull(result.getTaskList());

        verify(entityManager, times(1)).getReference(User.class, 1L);
        verify(entityManager, never()).getReference(Category.class, any());
        verify(entityManager, never()).getReference(Goal.class, any());
        verify(entityManager, never()).getReference(TaskList.class, any());
    }
}