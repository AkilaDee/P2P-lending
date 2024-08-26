package com.p2p.p2pbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p2p.p2pbackend.dto.SignInDto;
import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testSignIn() throws Exception {

        SignInDto signInDto = new SignInDto();
        signInDto.setEmail("user@example.com");
        signInDto.setPassword("password123");

        UserDto userDto = new UserDto();
        userDto.setEmail("user@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("user", userDto);
        mockResponse.put("role", "user");

        when(userService.signIn(signInDto)).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(signInDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("user@example.com"))
                .andExpect(jsonPath("$.user.firstName").value("John"))
                .andExpect(jsonPath("$.user.lastName").value("Doe"))
                .andExpect(jsonPath("$.role").value("user")); // Adjust to "admin" if that's the role set
    }
}
