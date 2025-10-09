package com.Ristretto.API.controller;

import com.Ristretto.API.domain.subTask.*;
import com.Ristretto.API.domain.task.Task;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("SubTask Controller Tests (Completo Revisado)")
class SubTaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubTaskService service;

    @InjectMocks
    private SubTaskController controller;

    private SubTask mockSubTask;
    private Task mockTask;
    private SubTaskRegistrationData registrationData;
    private SubTaskUpdateData updateData;
    private final Long subTaskId = 1L;
    private final Long taskId = 1L;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(pageableResolver)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        mockTask = new Task();
        mockSubTask = new SubTask();
        mockSubTask.setId(subTaskId);
        mockSubTask.setTitle("Write Introduction");
        mockSubTask.setDescription("Write the introduction chapter");
        mockSubTask.setDueDate(LocalDateTime.now().plusDays(3));
        mockSubTask.setIsDone(false);
        mockSubTask.setTask(mockTask);

        registrationData = new SubTaskRegistrationData(
                "Write Introduction",
                "Write the introduction chapter",
                LocalDateTime.now().plusDays(3),
                taskId
        );

        updateData = new SubTaskUpdateData(
                subTaskId,
                "Updated SubTask",
                true,
                "Updated description",
                LocalDateTime.now().plusDays(5)
        );
    }

    // ---------- REGISTRO ----------
    @Test
    void register_ShouldReturnCreatedSubTask_WhenValidDataProvided() throws Exception {
        when(service.convertToEntity(any(SubTaskRegistrationData.class))).thenReturn(mockSubTask);
        when(service.save(any(SubTask.class))).thenReturn(mockSubTask);

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service, times(1)).convertToEntity(any());
        verify(service, times(1)).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenTaskIdIsNull() throws Exception {
        SubTaskRegistrationData invalidData = new SubTaskRegistrationData(
                "Write Introduction", "Description", LocalDateTime.now().plusDays(3), null
        );

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldAcceptSubTask_WithoutDescriptionOrDueDate() throws Exception {
        SubTaskRegistrationData minimalData = new SubTaskRegistrationData(
                "Write Introduction", null, null, taskId
        );

        when(service.convertToEntity(any(SubTaskRegistrationData.class))).thenReturn(mockSubTask);
        when(service.save(any(SubTask.class))).thenReturn(mockSubTask);

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalData)))
                .andExpect(status().isCreated());

        verify(service, times(1)).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenTitleIsEmpty() throws Exception {
        SubTaskRegistrationData invalidData = new SubTaskRegistrationData(
                "", "Description", LocalDateTime.now().plusDays(3), taskId
        );

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenDueDateIsInPast() throws Exception {
        SubTaskRegistrationData invalidData = new SubTaskRegistrationData(
                "Title", "Description", LocalDateTime.now().minusDays(1), taskId
        );

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"title\":\"Intro\",\"dueDate\":}";

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    // ---------- LISTAGEM ----------
    @Test
    void listing_ShouldReturnPageOfSubTasks_WhenTaskIdProvided() throws Exception {
        var page = new PageImpl<>(Arrays.asList(mockSubTask), PageRequest.of(0, 10), 1);

        when(service.findByTaskId(any(), eq(taskId))).thenReturn(page);

        mockMvc.perform(get("/subtask")
                        .param("taskId", taskId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findByTaskId(any(), eq(taskId));
    }

    @Test
    void listing_ShouldReturnEmptyPage_WhenNoSubTasksFound() throws Exception {
        var page = new PageImpl<SubTask>(Arrays.asList(), PageRequest.of(0, 10), 0);

        when(service.findByTaskId(any(), eq(taskId))).thenReturn(page);

        mockMvc.perform(get("/subtask")
                        .param("taskId", taskId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(service, times(1)).findByTaskId(any(), eq(taskId));
    }

    @Test
    void listing_ShouldReturnBadRequest_WhenTaskIdNotProvided() throws Exception {
        mockMvc.perform(get("/subtask"))
                .andExpect(status().isBadRequest());

        verify(service, never()).findByTaskId(any(), any());
    }

    @Test
    void listing_ShouldReturnBadRequest_WhenPaginationInvalid() throws Exception {
        mockMvc.perform(get("/subtask")
                        .param("taskId", taskId.toString())
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/subtask")
                        .param("taskId", taskId.toString())
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(service, never()).findByTaskId(any(), any());
    }

    // ---------- ATUALIZAÇÃO ----------
    @Test
    void update_ShouldReturnUpdatedSubTask_WhenValidDataProvided() throws Exception {
        when(service.findById(subTaskId)).thenReturn(mockSubTask);
        when(service.save(any(SubTask.class))).thenReturn(mockSubTask);

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(service, times(1)).findById(subTaskId);
        verify(service, times(1)).save(any());
    }

    @Test
    void update_ShouldAllowPartialOrAllFieldsNull() throws Exception {
        SubTaskUpdateData partialData = new SubTaskUpdateData(
                subTaskId,
                null,
                null,
                null,
                null
        );

        when(service.findById(subTaskId)).thenReturn(mockSubTask);
        when(service.save(any(SubTask.class))).thenReturn(mockSubTask);

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialData)))
                .andExpect(status().isOk());

        verify(service, times(1)).findById(subTaskId);
        verify(service, times(1)).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsNullOrNegative() throws Exception {
        SubTaskUpdateData invalidDataNull = new SubTaskUpdateData(null, "Title", true, "Desc", LocalDateTime.now());
        SubTaskUpdateData invalidDataNegative = new SubTaskUpdateData(-1L, "Title", true, "Desc", LocalDateTime.now());

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDataNull)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDataNegative)))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"id\":1,\"title\":\"Update\",\"dueDate\":}";

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }

    @Test
    void update_ShouldReturnNotFound_WhenSubTaskNotFound() throws Exception {
        when(service.findById(subTaskId))
                .thenThrow(new com.Ristretto.API.infra.exception.Domain.ResourceNotFoundException("SubTask não encontrada"));

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));

        verify(service, times(1)).findById(subTaskId);
        verify(service, never()).save(any());
    }

    @Test
    void update_ShouldReturnInternalServerError_WhenServiceThrowsUnexpectedException() throws Exception {
        when(service.findById(subTaskId)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isInternalServerError());

        verify(service, times(1)).findById(subTaskId);
        verify(service, never()).save(any());
    }

    // ---------- DELETE ----------
    @Test
    void delete_ShouldReturnNoContent_WhenSubTaskExists() throws Exception {
        when(service.existsById(subTaskId)).thenReturn(true);
        doNothing().when(service).deleteById(subTaskId);

        mockMvc.perform(delete("/subtask/{id}", subTaskId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).existsById(subTaskId);
        verify(service, times(1)).deleteById(subTaskId);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenSubTaskNotFound() throws Exception {
        when(service.existsById(subTaskId)).thenReturn(false);

        mockMvc.perform(delete("/subtask/{id}", subTaskId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).existsById(subTaskId);
        verify(service, never()).deleteById(subTaskId);
    }

    @Test
    void delete_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        when(service.existsById(subTaskId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(service).deleteById(subTaskId);

        mockMvc.perform(delete("/subtask/{id}", subTaskId))
                .andExpect(status().isInternalServerError());

        verify(service, times(1)).existsById(subTaskId);
        verify(service, times(1)).deleteById(subTaskId);
    }

    @Test
    void delete_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        mockMvc.perform(delete("/subtask/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Parâmetro")));

        verify(service, never()).deleteById(any());
    }
}