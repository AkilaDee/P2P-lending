package com.p2p.p2pbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p2p.p2pbackend.dto.LendRequestDto;
import com.p2p.p2pbackend.dto.SignInDto;
import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    public void testSignIn_ReturnsResponse() {

        SignInDto signInDto = new SignInDto();
        signInDto.setEmail("test@example.com");
        signInDto.setPassword("testPassword");

        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("userId", 1);
        mockResponse.put("role", "USER");

        when(userService.signIn(signInDto)).thenReturn(mockResponse);

        ResponseEntity<Map<String, Object>> response = userController.signIn(signInDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse.get("userId"), response.getBody().get("userId"));
        assertEquals(mockResponse.get("role"), response.getBody().get("role"));

        verify(userService, times(1)).signIn(signInDto);
    }

    @Test
    public void testGetAllLendRequestsExcludingCurrentUser_ReturnsLendRequests() {
        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("userId", 1);

        List<Map<String, Object>> mockResponse = new ArrayList<>();
        Map<String, Object> lendRequestMap = new HashMap<>();
        lendRequestMap.put("lendRequestId", 101);
        lendRequestMap.put("userId", 2);
        lendRequestMap.put("amount", 1000.0);
        lendRequestMap.put("total", 1100.0);
        lendRequestMap.put("interestRate", 10.0);
        lendRequestMap.put("repaymentPeriod", 12);
        lendRequestMap.put("status", "PENDING");
        lendRequestMap.put("requestedUserId", 2);
        lendRequestMap.put("requestedByFirstName", "John");
        lendRequestMap.put("requestedByLastName", "Doe");

        mockResponse.add(lendRequestMap);

        when(userService.getAllLendRequestsExcludingCurrentUser(requestMap)).thenReturn((List<Object>) (List<?>) mockResponse);

        ResponseEntity<List<Object>> response = userController.getAllLendRequestsExcludingCurrentUser(requestMap);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get(0);
        assertEquals(101, responseBody.get("lendRequestId"));
        assertEquals("John", responseBody.get("requestedByFirstName"));

        verify(userService, times(1)).getAllLendRequestsExcludingCurrentUser(requestMap);
    }

    @Test
    public void testSubmitLendRequest_ReturnsCreatedLendRequest() {

        LendRequestDto lendRequestDto = new LendRequestDto();
        lendRequestDto.setLendRequestId(101);
        lendRequestDto.setUserId(1);
        lendRequestDto.setAmount(1000.0);
        lendRequestDto.setTotal(1100.0);
        lendRequestDto.setInterestRate(10.0);
        lendRequestDto.setRepaymentPeriod(12);
        lendRequestDto.setStatus("PENDING");

        when(userService.submitLendRequest(lendRequestDto)).thenReturn(lendRequestDto);

        ResponseEntity<LendRequestDto> response = userController.submitLendRequest(lendRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(lendRequestDto, response.getBody());

        verify(userService, times(1)).submitLendRequest(lendRequestDto);
    }

    @Test
    public void testAcceptLendRequest_ReturnsUpdatedLendRequest() {

        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("lendRequestId", 101);
        requestMap.put("userId", 1);

        LendRequestDto lendRequestDto = new LendRequestDto();
        lendRequestDto.setLendRequestId(101);
        lendRequestDto.setUserId(1);
        lendRequestDto.setAmount(1000.0);
        lendRequestDto.setTotal(1100.0);
        lendRequestDto.setInterestRate(10.0);
        lendRequestDto.setRepaymentPeriod(12);
        lendRequestDto.setStatus("ACCEPTED");

        when(userService.acceptLendRequest(requestMap)).thenReturn(lendRequestDto);

        ResponseEntity<LendRequestDto> response = userController.acceptLendRequest(requestMap);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lendRequestDto, response.getBody());

        verify(userService, times(1)).acceptLendRequest(requestMap);
    }

    @Test
    public void testGetAllPendingLendRequestsForUser_ReturnsPendingRequests() {

        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("userId", 1);

        List<LendRequestDto> lendRequestDtos = new ArrayList<>();
        LendRequestDto lendRequestDto1 = new LendRequestDto();
        lendRequestDto1.setLendRequestId(101);
        lendRequestDto1.setUserId(1);
        lendRequestDto1.setAmount(500.0);
        lendRequestDto1.setStatus("PENDING");

        LendRequestDto lendRequestDto2 = new LendRequestDto();
        lendRequestDto2.setLendRequestId(102);
        lendRequestDto2.setUserId(1);
        lendRequestDto2.setAmount(1000.0);
        lendRequestDto2.setStatus("PENDING");

        lendRequestDtos.add(lendRequestDto1);
        lendRequestDtos.add(lendRequestDto2);

        when(userService.getAllPendingLendRequestsForUser(requestMap)).thenReturn(lendRequestDtos);

        ResponseEntity<List<LendRequestDto>> response = userController.getAllPendingLendRequestsForUser(requestMap);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(lendRequestDtos.size(), response.getBody().size());
        assertEquals(lendRequestDtos.get(0).getLendRequestId(), response.getBody().get(0).getLendRequestId());

        verify(userService, times(1)).getAllPendingLendRequestsForUser(requestMap);
    }

    @Test
    public void testDeleteLendRequest_ReturnsNoContent() {

        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("lendRequestId", 101);

        doNothing().when(userService).deleteLendRequest(requestMap);

        ResponseEntity<Void> response = userController.deleteLendRequest(requestMap);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(userService, times(1)).deleteLendRequest(requestMap);
    }

    @Test
    public void testSubmitLoanRequestRating_ReturnsRatedUser() {

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("userId", 1);
        requestMap.put("loanRequestId", 1001);
        requestMap.put("rating", 5);

        UserDto mockRatedUser = new UserDto();
        mockRatedUser.setUserId(1);
        mockRatedUser.setFirstName("John");
        mockRatedUser.setLastName("Doe");
        mockRatedUser.setRating(5);

        when(userService.submitLoanRequestRating(requestMap)).thenReturn(mockRatedUser);

        ResponseEntity<UserDto> response = userController.submitLoanRequestRating(requestMap);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRatedUser, response.getBody());
        assertEquals(5, response.getBody().getRating());

        verify(userService, times(1)).submitLoanRequestRating(requestMap);
    }
}
