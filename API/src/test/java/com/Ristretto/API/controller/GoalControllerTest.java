package com.Ristretto.API.controller;

import com.Ristretto.API.domain.goal.*;
import com.Ristretto.API.domain.user.User;
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

@DisplayName("Goal Controller Tests (Completo Revisado)")
class GoalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GoalService service;

    @InjectMocks
    private GoalController controller;

    private Goal mockGoal;
    private User mockUser;
    private GoalRegistrationData registrationData;
    private GoalUpdateData updateData;
    private final Long goalId = 1L;
    private final Long userId = 1L;
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

        mockUser = new User();
        mockGoal = new Goal(
                goalId,
                "Complete Project",
                "Finish the TCC project",
                LocalDateTime.now(),
                LocalDateTime.now().plusMonths(3),
                mockUser,
                Arrays.asList()
        );

        registrationData = new GoalRegistrationData(
                "Complete Project",
                "Finish the TCC project",
                LocalDateTime.now().plusMonths(3),
                userId
        );

        updateData = new GoalUpdateData(
                goalId,
                "Updated Title",
                "Updated Description",
                LocalDateTime.now().plusMonths(6)
        );
    }

    // ---------- REGISTRO ----------
    @Test
    void register_ShouldReturnCreatedGoal_WhenValidDataProvided() throws Exception {
        when(service.convertToEntity(any(GoalRegistrationData.class))).thenReturn(mockGoal);
        when(service.save(any(Goal.class))).thenReturn(mockGoal);

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(goalId));

        verify(service, times(1)).convertToEntity(any());
        verify(service, times(1)).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenInvalidDataProvided() throws Exception {
        GoalRegistrationData invalidData = new GoalRegistrationData(
                "",
                "Description",
                LocalDateTime.now().plusMonths(3),
                userId
        );

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
        verify(service, never()).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenUserIdIsNull() throws Exception {
        GoalRegistrationData invalidData = new GoalRegistrationData(
                "Complete Project",
                "Finish the TCC project",
                LocalDateTime.now().plusMonths(3),
                null
        );

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenDueDateIsInPast() throws Exception {
        GoalRegistrationData pastDateData = new GoalRegistrationData(
                "Complete Project",
                "Finish the TCC project",
                LocalDateTime.now().minusDays(1),
                userId
        );

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pastDateData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldAcceptGoal_WithoutDescription() throws Exception {
        GoalRegistrationData minimalData = new GoalRegistrationData(
                "Complete Project",
                null,
                LocalDateTime.now().plusMonths(3),
                userId
        );

        when(service.convertToEntity(any(GoalRegistrationData.class))).thenReturn(mockGoal);
        when(service.save(any(Goal.class))).thenReturn(mockGoal);

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalData)))
                .andExpect(status().isCreated());

        verify(service, times(1)).convertToEntity(any());
    }

    @Test
    void register_ShouldAcceptGoal_WithoutDueDate() throws Exception {
        GoalRegistrationData dataWithoutDate = new GoalRegistrationData(
                "Ongoing Goal",
                "No specific deadline",
                null,
                userId
        );

        when(service.convertToEntity(any(GoalRegistrationData.class))).thenReturn(mockGoal);
        when(service.save(any(Goal.class))).thenReturn(mockGoal);

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dataWithoutDate)))
                .andExpect(status().isCreated());

        verify(service, times(1)).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenServiceThrowsIllegalArgumentException() throws Exception {
        when(service.convertToEntity(any(GoalRegistrationData.class)))
                .thenThrow(new IllegalArgumentException("User with id 999 not found"));

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("\"User with id 999 not found\""));

        verify(service, times(1)).convertToEntity(any());
        verify(service, never()).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"title\":\"Goal\",\"dueDate\":}";

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    // ---------- LISTAGEM ----------
    @Test
    void listing_ShouldReturnPageOfGoals_WhenUserIdProvided() throws Exception {
        var page = new PageImpl<>(Arrays.asList(mockGoal), PageRequest.of(0, 10), 1);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/goal")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(goalId))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

    @Test
    void listing_ShouldReturnEmptyPage_WhenNoGoalsFound() throws Exception {
        var page = new PageImpl<Goal>(Arrays.asList(), PageRequest.of(0, 10), 0);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/goal")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

    @Test
    void listing_ShouldAcceptCustomPageSize() throws Exception {
        var page = new PageImpl<Goal>(Arrays.asList(mockGoal), PageRequest.of(0, 50), 1);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/goal")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(50));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

    @Test
    void listing_ShouldReturnBadRequest_WhenPaginationInvalid() throws Exception {
        mockMvc.perform(get("/goal")
                        .param("userId", userId.toString())
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/goal")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(service, never()).findByUserId(any(), any());
    }

    // ---------- ATUALIZAÇÃO ----------
    @Test
    void update_ShouldReturnUpdatedGoal_WhenValidDataProvided() throws Exception {
        when(service.findById(goalId)).thenReturn(mockGoal);
        when(service.save(any(Goal.class))).thenReturn(mockGoal);

        mockMvc.perform(put("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(goalId));

        verify(service, times(1)).findById(goalId);
        verify(service, times(1)).save(any());
    }

    @Test
    void update_ShouldAllowPartialUpdate_WhenOnlySomeFieldsProvided() throws Exception {
        GoalUpdateData partialUpdate = new GoalUpdateData(
                goalId,
                null,
                null,
                LocalDateTime.now().plusMonths(9)
        );

        when(service.findById(goalId)).thenReturn(mockGoal);
        when(service.save(any(Goal.class))).thenReturn(mockGoal);

        mockMvc.perform(put("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isOk());

        verify(service, times(1)).findById(goalId);
    }

    @Test
    void update_ShouldReturnBadRequest_WhenInvalidDataProvided() throws Exception {
        GoalUpdateData invalidUpdateData = new GoalUpdateData(
                null,
                "Updated Title",
                "Updated Description",
                LocalDateTime.now().plusMonths(6)
        );

        mockMvc.perform(put("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdateData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
        verify(service, never()).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(put("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"id\":1,\"title\":\"Update\",\"dueDate\":}";

        mockMvc.perform(put("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }

    @Test
    void update_ShouldReturnInternalServerError_WhenGoalNotFound() throws Exception {
        when(service.findById(goalId)).thenThrow(new RuntimeException("Goal not found"));

        mockMvc.perform(put("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isInternalServerError());

        verify(service, times(1)).findById(goalId);
        verify(service, never()).save(any());
    }

    @Test
    void update_ShouldReturnNotFound_WhenGoalNotFound() throws Exception {
        when(service.findById(goalId))
                .thenThrow(new com.Ristretto.API.infra.exception.Domain.ResourceNotFoundException("Meta não encontrada"));

        mockMvc.perform(put("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));

        verify(service, times(1)).findById(goalId);
        verify(service, never()).save(any());
    }

    // ---------- DELETE ----------
    @Test
    void delete_ShouldReturnNoContent_WhenGoalExists() throws Exception {
        when(service.existsById(goalId)).thenReturn(true);
        doNothing().when(service).deleteById(goalId);

        mockMvc.perform(delete("/goal/{id}", goalId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).existsById(goalId);
        verify(service, times(1)).deleteById(goalId);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenGoalNotFound() throws Exception {
        when(service.existsById(goalId)).thenReturn(false);

        mockMvc.perform(delete("/goal/{id}", goalId))
                .andExpect(status().isNotFound());

        verify(service, times(1)).existsById(goalId);
        verify(service, never()).deleteById(goalId);
    }

    @Test
    void delete_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        mockMvc.perform(delete("/goal/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Parâmetro")));

        verify(service, never()).deleteById(any());
    }

    @Test
    void delete_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        when(service.existsById(goalId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(service).deleteById(goalId);

        mockMvc.perform(delete("/goal/{id}", goalId))
                .andExpect(status().isInternalServerError());

        verify(service, times(1)).existsById(goalId);
        verify(service, times(1)).deleteById(goalId);
    }
}