package com.Ristretto.API.controller;

import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.Ristretto.API.infra.security.AuthenticationData;
import com.Ristretto.API.infra.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Authentication Controller Tests")
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private AuthenticationData authenticationData;
    private User mockUser;
    private Authentication mockAuthentication;
    private ObjectMapper objectMapper;
    private final String validToken = "valid.jwt.token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        authenticationData = new AuthenticationData("user@example.com", "SecurePassword123");
        mockUser = new User();
        mockUser.setEmail("user@example.com");
        mockAuthentication = mock(Authentication.class);
    }

    @Test
    @DisplayName("Deve retornar 200 e token JWT quando credenciais são válidas")
    void login_ShouldReturnOkWithToken_WhenCredentialsAreValid() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        when(tokenService.generateToken(mockUser)).thenReturn(validToken);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationData)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(validToken));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenService, times(1)).generateToken(mockUser);
    }

    @Test
    @DisplayName("Deve retornar 401 quando credenciais são inválidas")
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authenticationData)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando senha está vazia")
    void login_ShouldReturnBadRequest_WhenPasswordIsEmpty() throws Exception {
        AuthenticationData invalidData = new AuthenticationData("user@example.com", "");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando email está vazio")
    void login_ShouldReturnBadRequest_WhenEmailIsEmpty() throws Exception {
        AuthenticationData invalidData = new AuthenticationData("", "SecurePassword123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando email é inválido")
    void login_ShouldReturnBadRequest_WhenEmailFormatIsInvalid() throws Exception {
        AuthenticationData invalidData = new AuthenticationData("invalid-email", "SecurePassword123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando corpo da requisição está vazio")
    void login_ShouldReturnBadRequest_WhenBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Deve retornar 400 quando JSON está malformado")
    void login_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"email\":\"user@example.com\",\"password\":}";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Deve retornar 200 mesmo com email em maiúsculas")
    void login_ShouldReturnOk_WhenEmailHasUppercase() throws Exception {
        AuthenticationData emailWithUppercase = new AuthenticationData("User@Example.COM", "SecurePassword123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        when(tokenService.generateToken(mockUser)).thenReturn(validToken);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailWithUppercase)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(validToken));

        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenService, times(1)).generateToken(mockUser);
    }

    @Test
    void login_ShouldReturnBadRequest_WhenFieldsAreNull() throws Exception {
        AuthenticationData invalidData = new AuthenticationData(null, null);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    void login_ShouldReturnBadRequest_WhenPasswordTooShort() throws Exception {
        // senha inválida (menos de 6 caracteres)
        AuthenticationData invalidData = new AuthenticationData("user@example.com", "123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        // como a validação falhou, o manager nunca deve ser chamado
        verify(authenticationManager, never()).authenticate(any());
    }

}