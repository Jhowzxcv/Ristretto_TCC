package com.Ristretto.API.controller;

import com.Ristretto.API.domain.task.*;
import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskService service;

    @InjectMocks
    private TaskController controller;

    private Task mockTask;
    private TaskRegistrationData registrationData;
    private TaskUpdateData updateData;
    private final Long taskId = 1L;
    private final Long userId = 1L;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        User mockUser = new User();
        mockUser.setId(userId);

        mockTask = new Task();
        mockTask.setId(taskId);
        mockTask.setTitle("Complete Documentation");
        mockTask.setDescription("Write comprehensive documentation");
        mockTask.setDueDate(LocalDateTime.now().plusDays(7));
        mockTask.setPriority(Priority.HIGH);
        mockTask.setStatus(Status.IN_PROGRESS);
        mockTask.setFocusMode(true);
        mockTask.setIsDone(false);

        registrationData = new TaskRegistrationData(
                "Complete Documentation",
                "Write comprehensive documentation",
                LocalDateTime.now().plusDays(7),
                Priority.HIGH,
                Status.IN_PROGRESS,
                userId,
                1L, // categoryId
                1L, // goalId
                1L, // taskListId
                true,
                false
        );

        updateData = new TaskUpdateData(
                taskId,
                "Updated Task Title",
                "Updated description",
                LocalDateTime.now().plusDays(10),
                Priority.MEDIUM,
                Status.COMPLETED,
                1L,
                1L,
                1L,
                true
        );
    }

    // ---------- REGISTRO ----------
    @Test
    void register_ShouldReturnCreatedTask_WhenValidDataProvided() throws Exception {
        when(service.convertToEntity(any(TaskRegistrationData.class))).thenAnswer(invocation -> {
            TaskRegistrationData data = invocation.getArgument(0);
            Task task = new Task();
            task.setId(taskId);
            task.setTitle(data.title());
            task.setDescription(data.description());
            task.setDueDate(data.dueDate());
            task.setPriority(data.priority());
            task.setStatus(data.status());
            return task;
        });

        when(service.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(taskId))
                .andExpect(jsonPath("$.title").value("Complete Documentation"));

        verify(service, times(1)).convertToEntity(any(TaskRegistrationData.class));
        verify(service, times(1)).save(any(Task.class));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenMandatoryFieldsInvalid() throws Exception {
        TaskRegistrationData invalidTitle = new TaskRegistrationData(
                "", "Desc", LocalDateTime.now().plusDays(1), Priority.HIGH, Status.NOT_STARTED, userId,
                null, null, null, false, false
        );

        TaskRegistrationData invalidUser = new TaskRegistrationData(
                "Title", "Desc", LocalDateTime.now().plusDays(1), Priority.HIGH, Status.NOT_STARTED, null,
                null, null, null, false, false
        );

        TaskRegistrationData pastDate = new TaskRegistrationData(
                "Title", "Desc", LocalDateTime.now().minusDays(1), Priority.HIGH, Status.NOT_STARTED, userId,
                null, null, null, false, false
        );

        TaskRegistrationData[] invalids = {invalidTitle, invalidUser, pastDate};

        for (TaskRegistrationData data : invalids) {
            mockMvc.perform(post("/task")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(data)))
                    .andExpect(status().isBadRequest());
        }

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldAcceptTask_WithoutOptionalFields() throws Exception {
        when(service.convertToEntity(any(TaskRegistrationData.class))).thenAnswer(invocation -> {
            Task task = new Task();
            task.setId(taskId);
            task.setTitle("Minimal Task");
            task.setPriority(Priority.LOW);
            task.setStatus(Status.NOT_STARTED);
            return task;
        });

        when(service.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskRegistrationData minimalData = new TaskRegistrationData(
                "Minimal Task", null, null, Priority.LOW, Status.NOT_STARTED, userId,
                null, null, null, null, null
        );

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalData)))
                .andExpect(status().isCreated());

        verify(service, times(1)).convertToEntity(any());
        verify(service, times(1)).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenJsonIsEmpty() throws Exception {
        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPriorityInvalid() throws Exception {
        String invalidJson = """
        {"title":"Task","priority":"INVALID","status":"NOT_STARTED","userId":1}
        """;

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    // ---------- LISTAGEM ----------
    @Test
    void listing_ShouldReturnPageOfTasks_WhenUserIdProvided() throws Exception {
        var page = new PageImpl<>(Collections.singletonList(mockTask), PageRequest.of(0, 10), 1);

        when(service.findAllByUserId(eq(userId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/task")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(taskId))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findAllByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void listing_ShouldReturnEmptyPage_WhenNoTasksFound() throws Exception {
        var page = new PageImpl<Task>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.findAllByUserId(eq(userId), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/task")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(service, times(1)).findAllByUserId(eq(userId), any(Pageable.class));
    }

    @Test
    void listing_ShouldReturnBadRequest_WhenUserIdMissing() throws Exception {
        mockMvc.perform(get("/task")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(service, never()).findAll(any());
    }

    // ---------- ATUALIZAÇÃO ----------
    @Test
    void update_ShouldReturnUpdatedTask_WhenValidDataProvided() throws Exception {
        when(service.existsById(taskId)).thenReturn(true);
        when(service.findById(taskId)).thenReturn(mockTask);
        when(service.save(any(Task.class))).thenReturn(mockTask);

        mockMvc.perform(put("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId));

        verify(service, times(1)).existsById(taskId);
        verify(service, times(1)).findById(taskId);
        verify(service, times(1)).save(any(Task.class));
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsNull() throws Exception {
        TaskUpdateData invalidData = new TaskUpdateData(
                null, "Updated", "Desc", LocalDateTime.now(), Priority.HIGH, Status.NOT_STARTED,
                null, null, null, false
        );

        mockMvc.perform(put("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
        verify(service, never()).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        String json = """
            {"id":"abc","title":"Invalid"}
            """;

        mockMvc.perform(put("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }

    @Test
    void update_ShouldAllowPartialUpdate_WhenOnlySomeFieldsProvided() throws Exception {
        TaskUpdateData partialData = new TaskUpdateData(
                taskId, "New Title", null, null, null, null, null, null, null, null
        );

        when(service.existsById(taskId)).thenReturn(true);
        when(service.findById(taskId)).thenReturn(mockTask);
        when(service.save(any(Task.class))).thenReturn(mockTask);

        mockMvc.perform(put("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialData)))
                .andExpect(status().isOk());

        verify(service, times(1)).existsById(taskId);
        verify(service, times(1)).findById(taskId);
        verify(service, times(1)).save(any());
    }

    @Test
    void update_ShouldReturnNotFound_WhenTaskNotFound() throws Exception {
        when(service.existsById(taskId)).thenReturn(false);

        mockMvc.perform(put("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound());

        verify(service, times(1)).existsById(taskId);
        verify(service, never()).findById(any());
        verify(service, never()).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenDueDateInPast() throws Exception {
        TaskUpdateData invalidData = new TaskUpdateData(
                taskId, "Title", "Desc", LocalDateTime.now().minusDays(1),
                Priority.HIGH, Status.NOT_STARTED, null, null, null, false
        );

        mockMvc.perform(put("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    // ---------- DELETE ----------
    @Test
    void delete_ShouldReturnNoContent_WhenTaskExists() throws Exception {
        when(service.existsById(taskId)).thenReturn(true);
        doNothing().when(service).deleteById(taskId);

        mockMvc.perform(delete("/task/{id}", taskId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).existsById(taskId);
        verify(service, times(1)).deleteById(taskId);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenTaskNotFound() throws Exception {
        when(service.existsById(taskId)).thenReturn(false);

        mockMvc.perform(delete("/task/{id}", taskId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));

        verify(service, times(1)).existsById(taskId);
        verify(service, never()).deleteById(taskId);
    }

    @Test
    void delete_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        mockMvc.perform(delete("/task/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Parâmetro")));

        verify(service, never()).deleteById(any());
    }
}