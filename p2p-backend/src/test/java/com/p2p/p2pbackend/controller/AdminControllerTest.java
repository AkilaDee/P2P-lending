package com.p2p.p2pbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    private MockMvc mockMvc;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllInactiveUsers_ReturnsInactiveUsers() {
        List<UserDto> inactiveUsers = new ArrayList<>();
        inactiveUsers.add(getTestUserDtoDetails(16));

        when(adminService.getAllInactiveUsers()).thenReturn(inactiveUsers);

        ResponseEntity<List<UserDto>> response = adminController.getAllInactiveUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Akila", response.getBody().get(0).getFirstName());

        verify(adminService, times(1)).getAllInactiveUsers();
    }

    @Test
    public void testGetAllInactiveUsers_ReturnsEmptyList() {
        when(adminService.getAllInactiveUsers()).thenReturn(new ArrayList<>());

        ResponseEntity<List<UserDto>> response = adminController.getAllInactiveUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().size());

        verify(adminService, times(1)).getAllInactiveUsers();
    }

    @Test
    public void testDisableUser_Success() {

        int userId = 1;
        UserDto disabledUser = new UserDto();
        disabledUser.setUserId(userId);
        disabledUser.setFirstName("Akila");
        disabledUser.setLastName("Malshan");
        disabledUser.setEmail("test@example.com");
        disabledUser.setActiveStatus(false);

        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("userId", userId);

        when(adminService.disableUsers(requestMap)).thenReturn(disabledUser);

        ResponseEntity<UserDto> response = adminController.disableUser(requestMap);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userId, response.getBody().getUserId());
        assertEquals(false, response.getBody().getActiveStatus());

        verify(adminService, times(1)).disableUsers(requestMap);
    }

    @Test
    public void testDisableUser_UserNotFound() {
        int userId = 1;
        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("userId", userId);

        when(adminService.disableUsers(requestMap)).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<UserDto> response = adminController.disableUser(requestMap);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertEquals(null, response.getBody());

        verify(adminService, times(1)).disableUsers(requestMap);
    }

    UserDto getTestUserDtoDetails(int userId) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setFirstName("Akila");
        userDto.setLastName("Malshan");
        userDto.setEmail("test@example.com");
        userDto.setActiveStatus(true);
        return userDto;
    }
}
