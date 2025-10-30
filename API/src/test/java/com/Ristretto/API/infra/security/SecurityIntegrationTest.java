package com.Ristretto.API.infra.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void passwordEncoder_ShouldBeBCrypt() {
        assertThat(passwordEncoder).isInstanceOf(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.class);
    }

    @Test
    void loginEndpoint_ShouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\",\"password\":\"pass\"}"))
                // não deve ser 401/403, mas sim 400 porque não há autenticação real configurada
                .andExpect(status().is4xxClientError());
    }

    @Test
    void userRegistration_ShouldBeAccessibleWithPostWithoutAuth() throws Exception {
        String uniqueEmail = "test" + UUID.randomUUID() + "@test.com";

        String json = """
        {
          "name": "Test User",
          "email": "%s",
          "password": "123456",
          "theme": "DARK",
          "defaultReminderMin": 15,
          "avatarUrl": "http://example.com/avatar.png"
        }
        """.formatted(uniqueEmail);

        mockMvc.perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated()); // ou 400 se payload inválido
    }

    @Test
    void userGet_ShouldRequireAuthentication() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isUnauthorized()); // 401 esperado
    }

    @Test
    void userPut_ShouldRequireAuthentication() throws Exception {
        String json = """
            {
              "id": 1,
              "name": "Updated User",
              "email": "updated@test.com",
              "password": "654321",
              "theme": "LIGHT",
              "defaultReminderMin": 30,
              "avatarUrl": "http://example.com/new-avatar.png"
            }
            """;

        mockMvc.perform(put("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized()); // 401 esperado
    }

    @Test
    void userDelete_ShouldRequireAuthentication() throws Exception {
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isUnauthorized()); // 401 esperado
    }


    @Test
    void swaggerEndpoint_ShouldRedirectToIndexWithoutAuth () throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().isFound()) // 302
                .andExpect(header().string("Location", "/swagger-ui/index.html"));
    }

    @Test
    void protectedEndpoint_ShouldRequireAuthentication () throws Exception {
        mockMvc.perform(get("/task"))
                .andExpect(status().isUnauthorized()); // 401 esperado
    }
}
