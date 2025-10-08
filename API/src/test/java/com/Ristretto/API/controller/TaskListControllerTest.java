package com.Ristretto.API.controller;

import com.Ristretto.API.domain.taskList.*;
import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Collections;

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

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        mockUser = new User();
        mockTaskList = new TaskList();
        mockTaskList.setId(taskListId);
        mockTaskList.setTitle("Work Tasks");
        mockTaskList.setColor("blue");
        mockTaskList.setIcon("icon.png");
        mockTaskList.setType(ListType.STANDARD);
        mockTaskList.setUser(mockUser);

        registrationData = new TaskListRegistrationData(
                "Work Tasks", "blue", "icon.png", ListType.STANDARD,
                null, null, false, false, LocalDateTime.now().plusDays(7), userId
        );

        updateData = new TaskListUpdateData(
                taskListId, "Personal Tasks", "green", "icon2.png", ListType.STANDARD,
                null, null, false, false, LocalDateTime.now().plusDays(10)
        );
    }

    // ---------- LISTAGEM ----------
    @Test
    void listing_ShouldReturnPageOfTaskLists_WhenCalled() throws Exception {
        var page = new PageImpl<>(Collections.singletonList(mockTaskList), PageRequest.of(0, 10), 1);
        when(service.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/tasklists")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findAll(any());
    }

    @Test
    void listing_ShouldReturnEmptyPage_WhenNoTaskListsFound() throws Exception {
        var emptyPage = new PageImpl<TaskList>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.findAll(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/tasklists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(service, times(1)).findAll(any());
    }

    // ---------- DETALHES ----------
    @Test
    void details_ShouldReturnTaskList_WhenTaskListExists() throws Exception {
        when(service.findById(taskListId)).thenReturn(mockTaskList);

        mockMvc.perform(get("/tasklists/{id}", taskListId))
                .andExpect(status().isOk());

        verify(service, times(1)).findById(taskListId);
    }

    @Test
    void details_ShouldReturnInternalServerError_WhenTaskListNotFound() throws Exception {
        when(service.findById(taskListId)).thenThrow(new RuntimeException("TaskList not found"));

        mockMvc.perform(get("/tasklists/{id}", taskListId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("TaskList not found")));

        verify(service, times(1)).findById(taskListId);
    }

    // ---------- REGISTRO ----------
    @Test
    void register_ShouldReturnCreatedTaskList_WhenValidDataProvided() throws Exception {
        TaskListRegistrationData validData = new TaskListRegistrationData(
                "Minha Lista",
                "#FF5733",
                "icon.png",
                ListType.STANDARD,
                null,
                null,
                false,
                false,
                LocalDateTime.now().plusDays(5),
                userId // precisa estar inicializado!
        );

        System.out.println(">>> DTO montado: " + validData);

        TaskList mockTaskList = mock(TaskList.class);
        when(mockTaskList.getId()).thenReturn(1L);

        when(service.convertToEntity(any(TaskListRegistrationData.class))).thenAnswer(invocation -> {
            TaskListRegistrationData arg = invocation.getArgument(0);
            System.out.println(">>> convertToEntity chamado com: " + arg);
            return mockTaskList;
        });

        when(service.save(any(TaskList.class))).thenAnswer(invocation -> {
            TaskList arg = invocation.getArgument(0);
            System.out.println(">>> save chamado com TaskList: " + arg);
            return mockTaskList;
        });

        mockMvc.perform(post("/tasklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validData)))
                .andDo(result -> {
                    System.out.println(">>> HTTP Status: " + result.getResponse().getStatus());
                    System.out.println(">>> Response Headers: " + result.getResponse().getHeaderNames());
                    System.out.println(">>> Location Header: " + result.getResponse().getHeader("Location"));
                    System.out.println(">>> Response Body: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service, times(1)).convertToEntity(any());
        verify(service, times(1)).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenMandatoryFieldsInvalid() throws Exception {
        TaskListRegistrationData[] invalids = {
                new TaskListRegistrationData("", "blue", "icon.png", ListType.STANDARD, null, null, false, false, null, userId),
                new TaskListRegistrationData("Work Tasks", "blue", "icon.png", null, null, null, false, false, null, userId),
                new TaskListRegistrationData("Work Tasks", "blue", "icon.png", ListType.STANDARD, null, null, false, false, null, null)
        };

        for (TaskListRegistrationData data : invalids) {
            mockMvc.perform(post("/tasklists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(data)))
                    .andExpect(status().isBadRequest());
        }

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldAcceptTaskList_WithMinimalFields() throws Exception {
        TaskListRegistrationData minimalData = new TaskListRegistrationData(
                "Simple List",
                null,
                null,
                ListType.STANDARD,
                null,
                null,
                false,
                false,
                LocalDateTime.now().plusDays(1), // deadline válido
                userId
        );

        when(service.convertToEntity(any(TaskListRegistrationData.class))).thenReturn(mockTaskList);
        when(service.save(any(TaskList.class))).thenReturn(mockTaskList);

        mockMvc.perform(post("/tasklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalData)))
                .andExpect(status().isCreated());

        verify(service, times(1)).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenJsonIsEmpty() throws Exception {
        mockMvc.perform(post("/tasklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenCyclicListWithoutRepetitionType() throws Exception {
        TaskListRegistrationData invalidData = new TaskListRegistrationData(
                "Cyclic List", "blue", "icon.png", ListType.CYCLIC,
                1, null, true, false, null, userId
        );

        mockMvc.perform(post("/tasklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());
    }


    // ---------- ATUALIZAÇÃO ----------
    @Test
    void update_ShouldReturnUpdatedTaskList_WhenValidDataProvided() throws Exception {
        when(service.findById(taskListId)).thenReturn(mockTaskList);
        when(service.save(any(TaskList.class))).thenReturn(mockTaskList);

        mockMvc.perform(put("/tasklists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(service, times(1)).findById(taskListId);
        verify(service, times(1)).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsNull() throws Exception {
        TaskListUpdateData invalidData = new TaskListUpdateData(
                null, "Title", "green", "icon2.png", ListType.STANDARD, null, null, false, false, null
        );

        mockMvc.perform(put("/tasklists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        String json = """
            {"id":"abc","title":"Invalid"}
            """;

        mockMvc.perform(put("/tasklists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }

    @Test
    void update_ShouldAllowPartialUpdate_WhenOnlySomeFieldsProvided() throws Exception {
        TaskListUpdateData partialData = new TaskListUpdateData(taskListId, "New Title", null, null, null, null, null, null, null, null);

        when(service.findById(taskListId)).thenReturn(mockTaskList);
        when(service.save(any(TaskList.class))).thenReturn(mockTaskList);

        mockMvc.perform(put("/tasklists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialData)))
                .andExpect(status().isOk());

        verify(service, times(1)).findById(taskListId);
        verify(service, times(1)).save(any());
    }

    @Test
    void update_ShouldReturnInternalServerError_WhenTaskListNotFound() throws Exception {
        when(service.findById(taskListId)).thenThrow(new RuntimeException("TaskList not found"));

        mockMvc.perform(put("/tasklists/{id}", taskListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("TaskList not found")));

        verify(service, times(1)).findById(taskListId);
        verify(service, never()).save(any());
    }

    // ---------- DELETE ----------
    @Test
    void delete_ShouldReturnNoContent_WhenTaskListExists() throws Exception {
        when(service.existsById(taskListId)).thenReturn(true);
        doNothing().when(service).deleteById(taskListId);

        mockMvc.perform(delete("/tasklists/{id}", taskListId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).existsById(taskListId);
        verify(service, times(1)).deleteById(taskListId);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenTaskListNotFound() throws Exception {
        when(service.existsById(taskListId)).thenReturn(false);

        mockMvc.perform(delete("/tasklists/{id}", taskListId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));

        verify(service, times(1)).existsById(taskListId);
        verify(service, never()).deleteById(taskListId);
    }

    @Test
    void delete_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        mockMvc.perform(delete("/tasklists/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Parâmetro")));

        verify(service, never()).deleteById(any());
    }
}