package com.Ristretto.API.domain.calendarEvent;

import com.Ristretto.API.domain.task.Task;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    @Mock
    private CalendarEventRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CalendarEventService calendarEventService;

    private User mockUser;
    private Task mockTask;
    private CalendarEvent mockCalendarEvent;
    private CalendarEventRegistrationData completeEventData;
    private CalendarEventRegistrationData eventDataWithoutTask;
    private CalendarEventRegistrationData minimalEventData;
    private final Long calendarEventId = 1L;
    private final Long userId = 1L;
    private final Long taskId = 2L;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockTask = new Task();
        mockCalendarEvent = new CalendarEvent();

        ReflectionTestUtils.setField(calendarEventService, "entityManager", entityManager);
        lenient().when(entityManager.getReference(User.class, userId)).thenReturn(mockUser);
        lenient().when(entityManager.getReference(Task.class, taskId)).thenReturn(mockTask);

        completeEventData = new CalendarEventRegistrationData(
                "Team Meeting",
                "Weekly team sync meeting",
                LocalDateTime.of(2025, 12, 15, 10, 0, 0),
                LocalDateTime.of(2025, 12, 15, 11, 0, 0),
                "America/Sao_Paulo",
                "#FF5733",
                userId,
                taskId,
                true,
                RepetitionType.WEEKLY,
                "FREQ=WEEKLY;BYDAY=MO",
                true
        );

        eventDataWithoutTask = new CalendarEventRegistrationData(
                "Personal Event",
                "Personal calendar event",
                LocalDateTime.of(2025, 12, 20, 14, 0, 0),
                LocalDateTime.of(2025, 12, 20, 15, 0, 0),
                "America/Sao_Paulo",
                "#33FF57",
                userId,
                null,
                false,
                null,
                null,
                false
        );

        minimalEventData = new CalendarEventRegistrationData(
                "Simple Event",
                null,
                LocalDateTime.of(2025, 12, 25, 9, 0, 0),
                LocalDateTime.of(2025, 12, 25, 10, 0, 0),
                null,
                null,
                userId,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void findByUserId_ShouldReturnPageOfCalendarEvents_WhenUserIdProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CalendarEvent> expectedPage = new PageImpl<>(Arrays.asList(mockCalendarEvent));
        when(repository.findByUserId(userId, pageable)).thenReturn(expectedPage);

        Page<CalendarEvent> result = calendarEventService.findByUserId(pageable, userId);

        assertEquals(expectedPage, result);
        verify(repository, times(1)).findByUserId(userId, pageable);
    }

    @Test
    void convertToEntity_ShouldCreateCompleteCalendarEvent_WhenCompleteDataProvided() {
        CalendarEvent result = calendarEventService.convertToEntity(completeEventData);

        assertEquals(completeEventData.title(), result.getTitle());
        assertEquals(completeEventData.description(), result.getDescription());
        assertEquals(completeEventData.startDate(), result.getStartDate());
        assertEquals(completeEventData.endDate(), result.getEndDate());
        assertEquals(completeEventData.timezone(), result.getTimezone());
        assertEquals(completeEventData.color(), result.getColor());
        assertEquals(completeEventData.repetitive(), result.getRepetitive());
        assertEquals(completeEventData.repetitionType(), result.getRepetitionType());
        assertEquals(completeEventData.repetitionRule(), result.getRepetitionRule());
        assertEquals(completeEventData.tactileReminder(), result.getTactileReminder());
        assertEquals(mockUser, result.getUser());
        assertEquals(mockTask, result.getTask());

        verify(entityManager, times(1)).getReference(User.class, userId);
        verify(entityManager, times(1)).getReference(Task.class, taskId);
    }

    @Test
    void convertToEntity_ShouldCreateCalendarEventWithoutTask_WhenTaskIdIsNull() {
        CalendarEvent result = calendarEventService.convertToEntity(eventDataWithoutTask);

        assertEquals(eventDataWithoutTask.title(), result.getTitle());
        assertEquals(eventDataWithoutTask.description(), result.getDescription());
        assertEquals(eventDataWithoutTask.startDate(), result.getStartDate());
        assertEquals(eventDataWithoutTask.endDate(), result.getEndDate());
        assertEquals(eventDataWithoutTask.timezone(), result.getTimezone());
        assertEquals(eventDataWithoutTask.color(), result.getColor());
        assertEquals(eventDataWithoutTask.repetitive(), result.getRepetitive());
        assertEquals(eventDataWithoutTask.repetitionType(), result.getRepetitionType());
        assertEquals(eventDataWithoutTask.repetitionRule(), result.getRepetitionRule());
        assertEquals(eventDataWithoutTask.tactileReminder(), result.getTactileReminder());
        assertEquals(mockUser, result.getUser());
        assertNull(result.getTask());

        verify(entityManager, times(1)).getReference(User.class, userId);
        verify(entityManager, never()).getReference(eq(Task.class), any());
    }

    @Test
    void convertToEntity_ShouldCreateMinimalCalendarEvent_WhenMinimalDataProvided() {
        CalendarEvent result = calendarEventService.convertToEntity(minimalEventData);

        assertEquals(minimalEventData.title(), result.getTitle());
        assertEquals(minimalEventData.description(), result.getDescription());
        assertEquals(minimalEventData.startDate(), result.getStartDate());
        assertEquals(minimalEventData.endDate(), result.getEndDate());
        assertEquals(minimalEventData.timezone(), result.getTimezone());
        assertEquals(minimalEventData.color(), result.getColor());
        assertEquals(minimalEventData.repetitive(), result.getRepetitive());
        assertEquals(minimalEventData.repetitionType(), result.getRepetitionType());
        assertEquals(minimalEventData.repetitionRule(), result.getRepetitionRule());
        assertEquals(minimalEventData.tactileReminder(), result.getTactileReminder());
        assertEquals(mockUser, result.getUser());
        assertNull(result.getTask());

        verify(entityManager, times(1)).getReference(User.class, userId);
        verify(entityManager, never()).getReference(eq(Task.class), any());
    }

    @Test
    void save_ShouldReturnSavedCalendarEvent_WhenCalendarEventProvided() {
        when(repository.save(mockCalendarEvent)).thenReturn(mockCalendarEvent);

        CalendarEvent result = calendarEventService.save(mockCalendarEvent);

        assertEquals(mockCalendarEvent, result);
        verify(repository, times(1)).save(mockCalendarEvent);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete_WhenIdProvided() {
        calendarEventService.deleteById(calendarEventId);

        verify(repository, times(1)).deleteById(calendarEventId);
    }
}