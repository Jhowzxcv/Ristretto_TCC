package com.Ristretto.API.controller;

import com.Ristretto.API.domain.taskList.*;
import com.Ristretto.API.domain.user.User;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskListControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TaskListService service;

    @InjectMocks
    private TaskListController controller;

    private TaskList mockTaskList;
    private User mockUser;
    private TaskListRegistrationData registrationData;
    private TaskListUpdateData updateData;
    private final Long taskListId = 1L;
    private final Long userId = 1L;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        mockUser = new User();
        mockTaskList = new TaskList();

        registrationData = new TaskListRegistrationData(
                "Work Tasks",
                "blue",
                "icon.png",
                ListType.STANDARD,
                null,
                null,
                false,
                false,
                LocalDateTime.now().plusDays(7),
                userId
        );

        updateData = new TaskListUpdateData(
                taskListId,
                "Personal Tasks",
                "green",
                "icon2.png",
                ListType.STANDARD,
                null,
                null,
                false,
                false,
                LocalDateTime.now().plusDays(10)
        );
    }

    @Test
    void listing_ShouldReturnPageOfTaskLists_WhenCalled() throws Exception {
        var taskLists = Arrays.asList(mockTaskList);
        var page = new PageImpl<>(taskLists, PageRequest.of(0, 10), 1);

        when(service.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/tasklists")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service).findAll(any());
    }

    @Test
    void details_ShouldReturnTaskList_WhenTaskListExists() throws Exception {
        when(service.findById(taskListId)).thenReturn(mockTaskList);

        mockMvc.perform(get("/tasklists/{id}", taskListId))
                .andExpect(status().isOk());

        verify(service).findById(taskListId);
    }

    @Test
    void register_ShouldReturnCreatedTaskList_WhenValidDataProvided() throws Exception {
        when(service.convertToEntity(any(TaskListRegistrationData.class))).thenReturn(mockTaskList);
        when(service.save(any(TaskList.class))).thenReturn(mockTaskList);

        mockMvc.perform(post("/tasklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service).convertToEntity(any());
        verify(service).save(any());
    }

    @Test
    void update_ShouldReturnUpdatedTaskList_WhenValidDataProvided() throws Exception {
        when(service.findById(taskListId)).thenReturn(mockTaskList);
        when(service.save(any(TaskList.class))).thenReturn(mockTaskList);

        mockMvc.perform(put("/tasklists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(service).findById(taskListId);
        verify(service).save(any());
    }

    @Test
    void delete_ShouldReturnNoContent_WhenTaskListExists() throws Exception {
        doNothing().when(service).deleteById(taskListId);

        mockMvc.perform(delete("/tasklists/{id}", taskListId))
                .andExpect(status().isNoContent());

        verify(service).deleteById(taskListId);
    }
}
