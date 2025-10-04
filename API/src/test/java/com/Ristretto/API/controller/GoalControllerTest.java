package com.Ristretto.API.controller;

import com.Ristretto.API.domain.goal.*;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

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

    @Test
    void register_ShouldReturnCreatedGoal_WhenValidDataProvided() throws Exception {
        when(service.convertToEntity(any(GoalRegistrationData.class))).thenReturn(mockGoal);
        when(service.save(any(Goal.class))).thenReturn(mockGoal);

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(goalId))
                .andExpect(jsonPath("$.title").value("Complete Project"))
                .andExpect(jsonPath("$.description").value("Finish the TCC project"));

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
    void register_ShouldReturnBadRequest_WhenIllegalArgumentExceptionThrown() throws Exception {
        when(service.convertToEntity(any(GoalRegistrationData.class)))
                .thenThrow(new IllegalArgumentException("Invalid user"));

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid user"));

        verify(service, times(1)).convertToEntity(any());
        verify(service, never()).save(any());
    }

    @Test
    void listing_ShouldReturnPageOfGoals_WhenUserIdProvided() throws Exception {
        var goals = Arrays.asList(mockGoal);
        var page = new PageImpl<>(goals, PageRequest.of(0, 10), 1);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/goal")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(goalId))
                .andExpect(jsonPath("$.content[0].title").value("Complete Project"))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

    @Test
    void listing_ShouldReturnEmptyPage_WhenNoGoalsFound() throws Exception {
        var page = new PageImpl<Goal>(Arrays.asList(), PageRequest.of(0, 10), 0);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/goal")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

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
    void delete_ShouldReturnNoContent_WhenGoalExists() throws Exception {
        doNothing().when(service).deleteById(goalId);

        mockMvc.perform(delete("/goal/{id}", goalId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(goalId);
    }

    @Test
    void delete_ShouldHandleException_WhenGoalNotFound() throws Exception {
        doThrow(new RuntimeException("Goal not found")).when(service).deleteById(goalId);

        mockMvc.perform(delete("/goal/{id}", goalId))
                .andExpect(status().isInternalServerError());

        verify(service, times(1)).deleteById(goalId);
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
    void register_ShouldReturnBadRequest_WhenUserIdIsNull() throws Exception {
        GoalRegistrationData noUserData = new GoalRegistrationData(
                "Complete Project",
                "Finish the TCC project",
                LocalDateTime.now().plusMonths(3),
                null
        );

        mockMvc.perform(post("/goal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noUserData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }
}
