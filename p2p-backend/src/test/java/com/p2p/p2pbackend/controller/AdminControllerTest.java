package com.p2p.p2pbackend.controller;

import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.entity.User;
import com.p2p.p2pbackend.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AdminControllerTest {

    @Mock
    private AdminService adminService;

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

    UserDto getTestUserDtoDetails(int userId) {
        UserDto userDto = new UserDto();
        userDto.setUserId(userId);
        userDto.setFirstName("Akila");
        userDto.setLastName("Malshan");
        userDto.setEmail("test@example.com");
        return userDto;
    }

}
