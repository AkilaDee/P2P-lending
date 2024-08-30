package com.p2p.p2pbackend.service;
import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.dto.LendRequestDto;
import com.p2p.p2pbackend.dto.LoanRequestDto;
import com.p2p.p2pbackend.entity.LendRequest;
import com.p2p.p2pbackend.entity.LoanRequest;
import com.p2p.p2pbackend.entity.User;
import com.p2p.p2pbackend.mapper.LendRequestMapper;
import com.p2p.p2pbackend.mapper.LoanRequestMapper;
import com.p2p.p2pbackend.mapper.UserMapper;
import com.p2p.p2pbackend.repository.LendRequestRepository;
import com.p2p.p2pbackend.repository.LoanRequestRepository;
import com.p2p.p2pbackend.repository.UserRepository;
import com.p2p.p2pbackend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private LendRequestRepository lendRequestRepository;

    @Mock
    private LoanRequestRepository loanRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private LoanRequestMapper loanRequestMapper;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Test
    void testGetAllPendingLendRequestsForUser() {

        User user = new User();
        user.setUserId(2);
        user.setFirstName("Akila");
        user.setLastName("Malshan");

        LendRequest lendRequest = new LendRequest();
        lendRequest.setLendRequestId(1);
        lendRequest.setUser(user);
        lendRequest.setAmount(1000.0);
        lendRequest.setTotal(1100.0);
        lendRequest.setInterestRate(0.1);
        lendRequest.setRepaymentPeriod(12);
        lendRequest.setStatus("PENDING");
        lendRequest.setCreatedAt(LocalDateTime.now().minusDays(1));
        lendRequest.setUpdatedAt(LocalDateTime.now());
        lendRequest.setAcceptedBy(null);

        List<LendRequest> lendRequests = Collections.singletonList(lendRequest);

        // Mock the repository behavior
        Mockito.when(lendRequestRepository.findByUser_UserIdAndStatus(2, "PENDING")).thenReturn(lendRequests);

        // Prepare the input map
        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("userId", 2); // The user ID to test with

        // Act: Call the service method
        List<LendRequestDto> response = userServiceImpl.getAllPendingLendRequestsForUser(requestMap);

        // Assert: Verify the response is as expected
        assertFalse(response.isEmpty(), "Expected non-empty response but got empty!");
        assertEquals(1, response.size(), "Expected one LendRequestDto in response");

        LendRequestDto dto = response.get(0);
        assertEquals(lendRequest.getLendRequestId(), dto.getLendRequestId());
        assertEquals(lendRequest.getUser().getUserId(), dto.getUserId());
        assertEquals(lendRequest.getAmount(), dto.getAmount());
        assertEquals(lendRequest.getTotal(), dto.getTotal());
        assertEquals(lendRequest.getInterestRate(), dto.getInterestRate());
        assertEquals(lendRequest.getRepaymentPeriod(), dto.getRepaymentPeriod());
        assertEquals(lendRequest.getStatus(), dto.getStatus());
        assertEquals(lendRequest.getCreatedAt(), dto.getCreatedAt());
        assertEquals(lendRequest.getUpdatedAt(), dto.getUpdatedAt());
    }

    @Test
    void testGetUserProfile_UserExists() {
        // Arrange: Prepare test data and mock behavior
        User user = new User();
        user.setUserId(1);
        user.setFirstName("Akila");
        user.setLastName("Malshan");
        user.setEmail("test@example.com");
        user.setPassword("secret"); // Password should be nullified in DTO

        UserDto userDto = new UserDto();
        userDto.setUserId(user.getUserId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(null); // Password should be null in the DTO

        // Mock UserRepository behavior
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Mock UserMapper
        Mockito.mockStatic(UserMapper.class);
        Mockito.when(UserMapper.mapToUserDto(user)).thenReturn(userDto);

        // Prepare the input map
        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("userId", 1); // The user ID to test with

        // Act: Call the service method
        UserDto response = userServiceImpl.getUserProfile(requestMap);

        // Assert: Verify the response is as expected
        assertEquals(1, response.getUserId());
        assertEquals("Akila", response.getFirstName());
        assertEquals("Malshan", response.getLastName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(null, response.getPassword()); // Ensure the password is null
    }

    @Test
    void testGetUserProfile_UserNotFound() {
        // Arrange: Prepare test data and mock behavior
        Mockito.when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Prepare the input map
        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("userId", 1); // The user ID to test with

        // Act & Assert: Verify that an exception is thrown
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userServiceImpl.getUserProfile(requestMap);
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testSubmitLoanRequest_UserExists() {
        // Arrange: Prepare test data and mock behavior
        User user = new User();
        user.setUserId(1);
        user.setFirstName("John");
        user.setLastName("Doe");

        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setUserId(1);
        loanRequestDto.setAmount(5000.0);
        loanRequestDto.setTotal(5500.0);
        loanRequestDto.setInterestRate(0.1);
        loanRequestDto.setRepaymentPeriod(12);

        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanRequestId(1);
        loanRequest.setUser(user);
        loanRequest.setAmount(5000.0);
        loanRequest.setTotal(5500.0);
        loanRequest.setInterestRate(0.1);
        loanRequest.setRepaymentPeriod(12);
        loanRequest.setStatus("PENDING");
        loanRequest.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        loanRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        LoanRequestDto savedLoanRequestDto = new LoanRequestDto();
        savedLoanRequestDto.setUserId(1);
        savedLoanRequestDto.setAmount(5000.0);
        savedLoanRequestDto.setTotal(5500.0);
        savedLoanRequestDto.setInterestRate(0.1);
        savedLoanRequestDto.setRepaymentPeriod(12);
        savedLoanRequestDto.setStatus("PENDING");

        // Mock UserRepository behavior
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Mock LoanRequestMapper methods
        Mockito.mockStatic(LoanRequestMapper.class);
        when(LoanRequestMapper.mapToLoanRequest(loanRequestDto, user, null)).thenReturn(loanRequest);
        when(LoanRequestMapper.mapToLoanRequestDto(loanRequest)).thenReturn(savedLoanRequestDto);

        // Mock LoanRequestRepository behavior
        when(loanRequestRepository.save(loanRequest)).thenReturn(loanRequest);

        // Act: Call the service method
        LoanRequestDto response = userServiceImpl.submitLoanRequest(loanRequestDto);

        // Assert: Verify the response is as expected
        assertEquals(savedLoanRequestDto.getUserId(), response.getUserId());
        assertEquals(savedLoanRequestDto.getAmount(), response.getAmount());
        assertEquals(savedLoanRequestDto.getTotal(), response.getTotal());
        assertEquals(savedLoanRequestDto.getInterestRate(), response.getInterestRate());
        assertEquals(savedLoanRequestDto.getRepaymentPeriod(), response.getRepaymentPeriod());
        assertEquals(savedLoanRequestDto.getStatus(), response.getStatus());
    }

    @Test
    void testSubmitLoanRequest_UserNotFound() {
        // Arrange: Prepare test data
        LoanRequestDto loanRequestDto = new LoanRequestDto();
        loanRequestDto.setUserId(1);

        // Mock UserRepository behavior
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert: Verify that an exception is thrown
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userServiceImpl.submitLoanRequest(loanRequestDto);
        });

        assertEquals("User not found", thrown.getMessage());
    }


    @Test
    void testSubmitLendRequestRating_UserNotFound() {
        // Arrange: Prepare test data
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("lendRequestId", 1);
        requestMap.put("userId", 1);
        requestMap.put("rating", 5.0);

        // Mock repository behavior
        when(userRepository.findByUserId(1)).thenReturn(Optional.empty());

        // Act & Assert: Verify that an exception is thrown
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userServiceImpl.submitLendRequestRating(requestMap);
        });

        assertEquals("User not found", thrown.getMessage());
    }

    @Test
    void testSubmitLendRequestRating_LendRequestNotFound() {
        // Arrange: Prepare test data
        User user = new User();
        user.setUserId(1);

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("lendRequestId", 1);
        requestMap.put("userId", 1);
        requestMap.put("rating", 5.0);

        // Mock repository behavior
        when(userRepository.findByUserId(1)).thenReturn(Optional.of(user));
        when(lendRequestRepository.findById(1)).thenReturn(Optional.empty());

        // Act & Assert: Verify that an exception is thrown
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            userServiceImpl.submitLendRequestRating(requestMap);
        });

        assertEquals("Lend request not found", thrown.getMessage());
    }


}
