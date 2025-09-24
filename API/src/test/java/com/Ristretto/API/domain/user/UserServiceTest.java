package com.Ristretto.API.domain.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserRegistrationData userRegistrationData;
    private User expectedUser;
    private User mockUser;
    private final String originalPassword = "test123";
    private final String hashedPassword = "hashedtest123";
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = new User();

        userRegistrationData = new UserRegistrationData(
                "test",
                "test123@testmail.com",
                originalPassword,
                Theme.DARK,
                Integer.valueOf(30),
                "http://testurl.com"
        );

        expectedUser = new User(
                "test",
                "test123@testmail.com",
                hashedPassword,
                Theme.DARK,
                Integer.valueOf(30),
                "http://testurl.com"
        );
    }

    @Test
    void createUser_ShouldReturnUserWithHashedPassword_WhenValidDataProvided() {
        when(passwordEncoder.encode(originalPassword)).thenReturn(hashedPassword);

        User result = userService.createUser(userRegistrationData);

        verify(passwordEncoder, times(1)).encode(originalPassword);

        assertEquals(expectedUser.getName(), result.getName());
        assertEquals(expectedUser.getEmail(), result.getEmail());
        assertEquals(expectedUser.getPassword(), result.getPassword());
        assertEquals(expectedUser.getTheme(), result.getTheme());
        assertEquals(expectedUser.getDefaultReminderMin(), result.getDefaultReminderMin());
        assertEquals(expectedUser.getAvatarUrl(), result.getAvatarUrl());

        assertEquals(hashedPassword, result.getPassword());
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        when(userRepository.getReferenceById(userId)).thenReturn(mockUser);

        User result = userService.findById(userId);

        assertEquals(mockUser, result);
        verify(userRepository, times(1)).getReferenceById(userId);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenUserExists() {
        when(userRepository.existsById(userId)).thenReturn(true);

        boolean result = userService.existsById(userId);

        assertTrue(result);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenUserDoesNotExist() {
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = userService.existsById(userId);

        assertFalse(result);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete_WhenIdProvided() {
        userService.deleteById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}