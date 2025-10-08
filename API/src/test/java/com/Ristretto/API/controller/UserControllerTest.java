package com.Ristretto.API.controller;

import com.Ristretto.API.domain.user.*;
import com.Ristretto.API.infra.exception.Domain.ResourceNotFoundException;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        mockUser = new User();
        mockUser.setId(userId);
        mockUser.setName("John Doe");
        mockUser.setEmail("john.doe@example.com");

        registrationData = new UserRegistrationData(
                "John Doe", "john.doe@example.com", "SecurePassword123",
                Theme.LIGHT, 15, "https://example.com/avatar.jpg"
        );

        updateData = new UserUpdateData(
                userId, "Jane Doe", Theme.DARK, 30, "https://example.com/new-avatar.jpg"
        );
    }

    // ---------- REGISTRO ----------
    @Test
    void register_ShouldReturnCreatedUser_WhenValidDataProvided() throws Exception {
        when(service.createUser(any(UserRegistrationData.class))).thenReturn(mockUser);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service, times(1)).createUser(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenMandatoryFieldsInvalid() throws Exception {
        UserRegistrationData[] invalids = {
                new UserRegistrationData("", "john.doe@example.com", "password", Theme.LIGHT, 15, null),
                new UserRegistrationData("John Doe", "invalid-email", "password", Theme.LIGHT, 15, null),
                new UserRegistrationData("John Doe", "john.doe@example.com", "", Theme.LIGHT, 15, null)
        };

        for (UserRegistrationData data : invalids) {
            mockMvc.perform(post("/user")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(data)))
                    .andExpect(status().isBadRequest());
        }

        verify(service, never()).createUser(any());
    }

    @Test
    void register_ShouldAcceptUser_WithoutAvatarUrl() throws Exception {
        UserRegistrationData dataWithoutAvatar = new UserRegistrationData(
                "John Doe", "john.doe@example.com", "SecurePassword123", Theme.LIGHT, 15, null
        );

        when(service.createUser(any(UserRegistrationData.class))).thenReturn(mockUser);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dataWithoutAvatar)))
                .andExpect(status().isCreated());

        verify(service, times(1)).createUser(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).createUser(any());
    }

    @Test
    void register_ShouldNotExposePasswordHash_InResponse() throws Exception {
        when(service.createUser(any(UserRegistrationData.class))).thenReturn(mockUser);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }


    // ---------- BUSCA ----------
    @Test
    void find_ShouldReturnUser_WhenUserExists() throws Exception {
        when(service.findById(userId)).thenReturn(mockUser);

        mockMvc.perform(get("/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(service, times(1)).findById(userId);
    }

    @Test
    void find_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(service.findById(userId)).thenThrow(new ResourceNotFoundException("\"Recurso não encontrado\""));

        mockMvc.perform(get("/user/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));

        verify(service, times(1)).findById(userId);
    }

    @Test
    void find_ShouldReturnBadRequest_WhenIdIsInvalid() throws Exception {
        mockMvc.perform(get("/user/{id}", "abc"))
                .andExpect(status().isBadRequest());
    }

    // ---------- ATUALIZAÇÃO ----------
    @Test
    void update_ShouldReturnUpdatedUser_WhenValidDataProvided() throws Exception {
        when(service.findById(userId)).thenReturn(mockUser);
        when(service.save(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));

        verify(service, times(1)).findById(userId);
        verify(service, times(1)).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsNull() throws Exception {
        UserUpdateData invalidData = new UserUpdateData(null, "Jane Doe", Theme.DARK, 30, null);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }

    @Test
    void update_ShouldAllowPartialUpdate() throws Exception {
        UserUpdateData partialData = new UserUpdateData(userId, "New Name", null, null, null);

        when(service.findById(userId)).thenReturn(mockUser);
        when(service.save(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialData)))
                .andExpect(status().isOk());

        verify(service, times(1)).findById(userId);
        verify(service, times(1)).save(any());
    }

    @Test
    void update_ShouldReturnNotFound_WhenUserNotFound() throws Exception {
        when(service.findById(userId)).thenThrow(new ResourceNotFoundException("\"Recurso não encontrado\""));

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));

        verify(service, times(1)).findById(userId);
        verify(service, never()).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).findById(any());
    }


    // ---------- DELETE ----------
    @Test
    void delete_ShouldReturnNoContent_WhenUserExists() throws Exception {
        when(service.existsById(userId)).thenReturn(true);
        doNothing().when(service).deleteById(userId);

        mockMvc.perform(delete("/user/{id}", userId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).existsById(userId);
        verify(service, times(1)).deleteById(userId);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(service.existsById(userId)).thenReturn(false);

        mockMvc.perform(delete("/user/{id}", userId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));

        verify(service, times(1)).existsById(userId);
        verify(service, never()).deleteById(any());
    }

    @Test
    void delete_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        mockMvc.perform(delete("/user/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Parâmetro")));

        verify(service, never()).deleteById(any());
    }
}