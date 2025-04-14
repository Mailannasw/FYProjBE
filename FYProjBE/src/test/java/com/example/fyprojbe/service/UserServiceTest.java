package com.example.fyprojbe.service;

import com.example.fyprojbe.model.User;
import com.example.fyprojbe.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    private User testUser;
    private final String TEST_USER_ID = "user123";
    private final String TEST_USERNAME = "testuser";
    private final String RAW_PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "encoded_password";

    // Before each test, create a new User object with test data
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setUsername(TEST_USERNAME);
        testUser.setPassword(ENCODED_PASSWORD);
    }

    @Test
    void createUser_Success() {
        // Given
        User inputUser = new User();
        inputUser.setUsername(TEST_USERNAME);
        inputUser.setPassword(RAW_PASSWORD);

        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser(inputUser);

        // Then
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getId());
        assertEquals(TEST_USERNAME, result.getUsername());
        assertEquals(ENCODED_PASSWORD, result.getPassword());
        verify(passwordEncoder).encode(RAW_PASSWORD);
        verify(userRepository).save(any(User.class));
    }


    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Given
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(testUser);

        // When
        UserDetails result = userService.loadUserByUsername(TEST_USERNAME);

        // Then
        assertNotNull(result);
        assertEquals(TEST_USERNAME, result.getUsername());
        assertEquals(ENCODED_PASSWORD, result.getPassword());
        verify(userRepository).findByUsername(TEST_USERNAME);
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsException() {
        // Given
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        // When
        Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                userService.loadUserByUsername(TEST_USERNAME)
        );

        // Then
        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByUsername(TEST_USERNAME);
    }
}