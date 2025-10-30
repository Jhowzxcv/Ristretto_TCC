package com.Ristretto.API.infra.security;

import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.infra.exception.Token.InvalidTokenException;
import com.Ristretto.API.infra.exception.Token.TokenGenerationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private User user;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();

        // Injetando valores de configuração via ReflectionTestUtils
        ReflectionTestUtils.setField(tokenService, "secret", "my-secret-key");
        ReflectionTestUtils.setField(tokenService, "expirationHours", 1L);

        user = new User();
        user.setId(1L); // ✅ agora o ID está definido
        user.setEmail("user@example.com");
    }

    @Test
    @DisplayName("Deve gerar token JWT válido para usuário")
    void generateToken_ShouldReturnValidToken() {
        String token = tokenService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("Deve extrair subject corretamente de token válido (ID do usuário)")
    void getSubject_ShouldReturnId_WhenTokenIsValid() {
        String token = tokenService.generateToken(user);

        String subject = tokenService.getSubject(token);

        assertEquals("1", subject); // ✅ subject é o ID
    }

    @Test
    @DisplayName("Deve lançar InvalidTokenException para token inválido")
    void getSubject_ShouldThrowException_WhenTokenIsInvalid() {
        String invalidToken = "invalid.token.value";

        assertThrows(InvalidTokenException.class,
                () -> tokenService.getSubject(invalidToken));
    }

    @Test
    @DisplayName("Deve lançar TokenGenerationException quando secret é inválido")
    void generateToken_ShouldThrowException_WhenSecretIsInvalid() {
        TokenService brokenService = new TokenService();
        ReflectionTestUtils.setField(brokenService, "secret", null); // força erro
        ReflectionTestUtils.setField(brokenService, "expirationHours", 1L);

        assertThrows(TokenGenerationException.class,
                () -> brokenService.generateToken(user));
    }

    @Test
    @DisplayName("Deve lançar InvalidTokenException quando token está expirado")
    void getSubject_ShouldThrowException_WhenTokenIsExpired() {
        TokenService shortLivedService = new TokenService();
        ReflectionTestUtils.setField(shortLivedService, "secret", "my-secret-key");
        ReflectionTestUtils.setField(shortLivedService, "expirationHours", -1L); // já expirado

        String expiredToken = shortLivedService.generateToken(user);

        assertThrows(InvalidTokenException.class,
                () -> shortLivedService.getSubject(expiredToken));
    }
}