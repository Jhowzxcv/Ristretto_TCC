package com.Ristretto.API.controller;

import com.Ristretto.API.domain.user.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService service;

    @InjectMocks
    private UserController controller;

    private User mockUser;
    private UserRegistrationData registrationData;
    private UserUpdateData updateData;
    private final Long userId = 1L;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("John Doe");
        mockUser.setEmail("john.doe@example.com");

        registrationData = new UserRegistrationData(
                "John Doe",
                "john.doe@example.com",
                "SecurePassword123",
                Theme.LIGHT,
                15,
                "https://example.com/avatar.jpg"
        );

        updateData = new UserUpdateData(
                userId,
                "Jane Doe",
                Theme.DARK,
                30,
                "https://example.com/new-avatar.jpg"
        );
    }

    // --- REGISTER TESTS ---

    @Test
    void register_ShouldReturnCreatedUser_WhenValidDataProvided() throws Exception {
        when(service.createUser(any(UserRegistrationData.class))).thenReturn(mockUser);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service).createUser(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
        UserRegistrationData invalidData = new UserRegistrationData(
                "", "john.doe@example.com", "SecurePassword123",
                Theme.LIGHT, 15, null
        );

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).createUser(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        UserRegistrationData invalidData = new UserRegistrationData(
                "John Doe", "invalid-email", "SecurePassword123",
                Theme.LIGHT, 15, null
        );

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).createUser(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenPasswordIsEmpty() throws Exception {
        UserRegistrationData invalidData = new UserRegistrationData(
                "John Doe", "john.doe@example.com", "",
                Theme.LIGHT, 15, null
        );

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).createUser(any());
    }

    // --- FIND TESTS ---

    @Test
    void find_ShouldReturnUser_WhenUserExists() throws Exception {
        when(service.findById(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(userId);
    }

    @Test
    void find_ShouldReturnInternalServerError_WhenUserDoesNotExist() throws Exception {
        when(service.findById(userId)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/user/{id}", userId))
                .andExpect(status().isInternalServerError());

        verify(service).findById(userId);
    }

    // --- UPDATE TESTS ---

    @Test
    void update_ShouldReturnUpdatedUser_WhenValidDataProvided() throws Exception {
        when(service.findById(userId)).thenReturn(mockUser);
        when(service.save(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).findById(userId);
        verify(service).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsNull() throws Exception {
        UserUpdateData invalidData = new UserUpdateData(
                null, "Jane Doe", Theme.DARK, 30, null
        );

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }

    @Test
    void update_ShouldAllowPartialUpdate_WhenOnlySomeFieldsProvided() throws Exception {
        UserUpdateData partialData = new UserUpdateData(
                userId,
                "New Name",
                null,
                null,
                null
        );

        when(service.findById(userId)).thenReturn(mockUser);
        when(service.save(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialData)))
                .andExpect(status().isOk());

        verify(service).findById(userId);
        verify(service).save(any());
    }

    // --- DELETE TESTS ---

    @Test
    void delete_ShouldReturnNoContent_WhenUserExists() throws Exception {
        when(service.existsById(userId)).thenReturn(true);
        doNothing().when(service).deleteById(userId);

        mockMvc.perform(delete("/user/{id}", userId))
                .andExpect(status().isNoContent());

        verify(service).existsById(userId);
        verify(service).deleteById(userId);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(service.existsById(userId)).thenReturn(false);

        mockMvc.perform(delete("/user/{id}", userId))
                .andExpect(status().isNotFound());

        verify(service).existsById(userId);
        verify(service, never()).deleteById(any());
    }
}
