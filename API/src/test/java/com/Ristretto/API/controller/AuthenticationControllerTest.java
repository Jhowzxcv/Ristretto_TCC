package com.Ristretto.API.controller;

import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.Ristretto.API.infra.security.AuthenticationData;
import com.Ristretto.API.infra.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Authentication Controller Tests")
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
    private final String validToken = "valid.jwt.token";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Incluindo o GlobalExceptionHandler para capturar BadCredentialsException
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        authenticationData = new AuthenticationData("user@example.com", "SecurePassword123");
        mockUser = new User();
        mockUser.setEmail("user@example.com");
        mockAuthentication = mock(Authentication.class);
    }

    @Test
    @DisplayName("Should return 200 and JWT token when credentials are valid")
    void login_ShouldReturnOkWithToken_WhenCredentialsAreValid() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);
        when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        when(tokenService.gerarToken(mockUser)).thenReturn(validToken);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"SecurePassword123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value(validToken));

        verify(authenticationManager).authenticate(argThat(token -> {
            if (!(token instanceof UsernamePasswordAuthenticationToken)) return false;
            UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) token;
            return authToken.getPrincipal().equals("user@example.com") &&
                    authToken.getCredentials().equals("SecurePassword123");
        }));
        verify(tokenService).gerarToken(mockUser);
    }

    @Test
    @DisplayName("Should return 401 when credentials are invalid")
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"SecurePassword123\"}"))
                .andExpect(status().isUnauthorized());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Should return 400 when password is empty")
    void login_ShouldReturnBadRequest_WhenPasswordIsEmpty() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
        verify(tokenService, never()).gerarToken(any());
    }

    @Test
    @DisplayName("Should return 400 when email is empty")
    void login_ShouldReturnBadRequest_WhenEmailIsEmpty() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"SecurePassword123\"}"))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
        verify(tokenService, never()).gerarToken(any());
    }
}
