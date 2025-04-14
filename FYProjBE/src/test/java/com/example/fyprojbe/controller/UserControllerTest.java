package com.example.fyprojbe.controller;

import com.example.fyprojbe.exceptions.GlobalExceptionHandler;
import com.example.fyprojbe.model.User;
import com.example.fyprojbe.service.UserService;
import com.example.fyprojbe.utils.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    @Mock private UserService userService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenUtil jwtTokenUtil;
    @InjectMocks private UserController userController;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Before each test, set up mock MVC consisting of the UserController and GlobalExceptionHandler
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createUser_ValidUser_ReturnsCreatedUser() throws Exception {
        // Given
        User inputUser = new User();
        inputUser.setUsername("newuser");
        inputUser.setPassword("password");

        User createdUser = createTestUser("newUserId", "newuser");

        // When
        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("newUserId")))
                .andExpect(jsonPath("$.username", is("newuser")));

        // Then
        verify(userService).createUser(any(User.class));
    }

    @Test
    void login_ValidCredentials_ReturnsToken() throws Exception {
        // Given
        User loginUser = new User();
        loginUser.setUsername("testuser");
        loginUser.setPassword("password");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("encoded_password")
                .authorities("USER")
                .build();

        Authentication authentication = mock(Authentication.class);

        // When
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtTokenUtil.generateToken(userDetails)).thenReturn("test.jwt.token");

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt", is("test.jwt.token")));

        // Then
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).loadUserByUsername("testuser");
        verify(jwtTokenUtil).generateToken(userDetails);
    }

    @Test
    void login_InvalidCredentials_ThrowsException() throws Exception {
        // Given
        User loginUser = new User();
        loginUser.setUsername("wronguser");
        loginUser.setPassword("wrongpass");

        // When
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isInternalServerError()); // Adjust based on your GlobalExceptionHandler behavior

        // Then
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never()).loadUserByUsername(anyString());
        verify(jwtTokenUtil, never()).generateToken(any(UserDetails.class));
    }

    private User createTestUser(String id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("encoded_password");
        return user;
    }
}