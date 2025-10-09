package com.Ristretto.API.controller;

import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.infra.security.AuthenticationData;
import com.Ristretto.API.infra.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String validToken = "valid.jwt.token";

    @BeforeEach
    void resetMocks() {
        Mockito.reset(authenticationManager, tokenService);
    }

    // ---------- LOGIN SUCESSO ----------
    @Test
    @DisplayName("Deve autenticar com sucesso e retornar token")
    void login_ShouldReturnOk_WhenCredentialsValid() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("user@example.com");

        Authentication mockAuth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);
        when(tokenService.generateToken(mockUser)).thenReturn(validToken);

        AuthenticationData data = new AuthenticationData("user@example.com", "SecurePassword123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(validToken));
    }

    // ---------- LOGIN FALHA ----------
    @Test
    @DisplayName("Deve retornar 401 quando credenciais são inválidas")
    void login_ShouldReturnUnauthorized_WhenCredentialsInvalid() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid"));

        AuthenticationData data = new AuthenticationData("user@example.com", "wrongpass");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    // ---------- VALIDAÇÃO DE CAMPOS ----------
    @ParameterizedTest(name = "Email: \"{0}\" | Senha: \"{1}\" deve falhar com 400")
    @CsvSource({
            "'', SecurePassword123",          // email vazio
            "invalid-email, SecurePassword123", // email inválido
            "user@example.com, ''",           // senha vazia
            "user@example.com, 123",          // senha curta
            "'', ''"                          // ambos vazios
    })
    void login_ShouldReturnBadRequest_WhenInvalidFields(String email, String password) throws Exception {
        AuthenticationData invalid = new AuthenticationData(email, password);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }

    // ---------- JSON MALFORMADO ----------
    @Test
    @DisplayName("Deve retornar 400 quando JSON está malformado")
    void login_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"email\":\"user@example.com\",\"password\":}";

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    // ---------- ERROS INTERNOS ----------
    @Test
    @DisplayName("Deve retornar 500 quando ocorre erro inesperado na autenticação")
    void login_ShouldReturnInternalServerError_WhenUnexpectedError() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        AuthenticationData data = new AuthenticationData("user@example.com", "SecurePassword123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Deve retornar 500 quando falha ao gerar token")
    void login_ShouldReturnInternalServerError_WhenTokenGenerationFails() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("user@example.com");

        Authentication mockAuth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);
        when(mockAuth.getPrincipal()).thenReturn(mockUser);
        when(tokenService.generateToken(mockUser)).thenThrow(new RuntimeException("Token error"));

        AuthenticationData data = new AuthenticationData("user@example.com", "SecurePassword123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isInternalServerError());
    }
}