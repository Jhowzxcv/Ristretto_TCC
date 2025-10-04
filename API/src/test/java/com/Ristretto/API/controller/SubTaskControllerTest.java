package com.Ristretto.API.controller;

import com.Ristretto.API.domain.subTask.*;
import com.Ristretto.API.domain.task.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        mockTask = new Task();
        mockSubTask = new SubTask();

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

    @Test
    void register_ShouldReturnCreatedSubTask_WhenValidDataProvided() throws Exception {
        when(service.convertToEntity(any(SubTaskRegistrationData.class))).thenReturn(mockSubTask);
        when(service.save(any(SubTask.class))).thenReturn(mockSubTask);

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service).convertToEntity(any());
        verify(service).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenTitleIsEmpty() throws Exception {
        SubTaskRegistrationData invalidData = new SubTaskRegistrationData(
                "",
                "Description",
                LocalDateTime.now().plusDays(3),
                taskId
        );

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenTaskIdIsNull() throws Exception {
        SubTaskRegistrationData invalidData = new SubTaskRegistrationData(
                "Write Introduction",
                "Description",
                LocalDateTime.now().plusDays(3),
                null
        );

        mockMvc.perform(post("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void listing_ShouldReturnPageOfSubTasks_WhenTaskIdProvided() throws Exception {
        var subTasks = Arrays.asList(mockSubTask);
        var page = new PageImpl<>(subTasks, PageRequest.of(0, 10), 1);

        when(service.findByTaskId(any(), eq(taskId))).thenReturn(page);

        mockMvc.perform(get("/subtask")
                        .param("taskId", taskId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service).findByTaskId(any(), eq(taskId));
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

        verify(service).findByTaskId(any(), eq(taskId));
    }

    @Test
    void update_ShouldReturnUpdatedSubTask_WhenValidDataProvided() throws Exception {
        when(service.findByid(subTaskId)).thenReturn(mockSubTask);
        when(service.save(any(SubTask.class))).thenReturn(mockSubTask);

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(service).findByid(subTaskId);
        verify(service).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsNull() throws Exception {
        SubTaskUpdateData invalidData = new SubTaskUpdateData(
                null,
                "Updated SubTask",
                true,
                "Description",
                LocalDateTime.now().plusDays(5)
        );

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).findByid(any());
    }

    @Test
    void update_ShouldAllowPartialUpdate_WhenOnlySomeFieldsProvided() throws Exception {
        SubTaskUpdateData partialData = new SubTaskUpdateData(
                subTaskId,
                "New Title",
                null,
                null,
                null
        );

        when(service.findByid(subTaskId)).thenReturn(mockSubTask);
        when(service.save(any(SubTask.class))).thenReturn(mockSubTask);

        mockMvc.perform(put("/subtask")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialData)))
                .andExpect(status().isOk());

        verify(service).findByid(subTaskId);
        verify(service).save(any());
    }

    @Test
    void delete_ShouldReturnNoContent_WhenSubTaskExists() throws Exception {
        doNothing().when(service).deleteById(subTaskId);

        mockMvc.perform(delete("/subtask/{id}", subTaskId))
                .andExpect(status().isNoContent());

        verify(service).deleteById(subTaskId);
    }

    @Test
    void delete_ShouldHandleException_WhenSubTaskNotFound() throws Exception {
        doThrow(new RuntimeException("SubTask not found")).when(service).deleteById(subTaskId);

        mockMvc.perform(delete("/subtask/{id}", subTaskId))
                .andExpect(status().isInternalServerError());

        verify(service).deleteById(subTaskId);
    }
}
