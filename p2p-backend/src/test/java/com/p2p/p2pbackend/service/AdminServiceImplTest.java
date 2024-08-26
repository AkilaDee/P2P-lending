package com.p2p.p2pbackend.service;


import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.entity.User;
import com.p2p.p2pbackend.mapper.UserMapper;
import com.p2p.p2pbackend.repository.UserRepository;
import com.p2p.p2pbackend.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AdminServiceImpl adminServiceImpl;

    @Mock
    private UserMapper userMapper;

    @BeforeAll
    public static void beforeAllTests() {
        UserDto userDto = new UserDto();
        userDto.setUserId(10);
        userDto.setFirstName("Akila");
        userDto.setLastName("Malshan");
        userDto.setEmail("test@example.com");

        // mock UserMapper static method
        MockedStatic<UserMapper> mockedStatic = mockStatic(UserMapper.class);
        mockedStatic.when(() -> UserMapper.mapToUserDto(any(User.class))).thenReturn(userDto);
    }

    @Test
    void testGetAllInactiveUsers() {

        // test data
        List<User> inactiveUsers = new ArrayList<>();
        inactiveUsers.add(getTestUserDetails(1));

        // mock repository
        Mockito.when(userRepository.findByActiveStatusFalse()).thenReturn(inactiveUsers);

        // call test method
        List<UserDto> userDtoList = adminServiceImpl.getAllInactiveUsers();

        // assertions
        Assertions.assertFalse(userDtoList.isEmpty());
        Assertions.assertEquals(inactiveUsers.get(0).getFirstName(), userDtoList.get(0).getFirstName());
    }

    @Test
    void testGetAllActiveUsers() {

        // test data
        List<User> activeUsers = new ArrayList<>();
        activeUsers.add(getTestUserDetails(1));

        // mock repository
        Mockito.when(userRepository.findByActiveStatusTrue()).thenReturn(activeUsers);

        // call test method
        List<UserDto> userDtoList = adminServiceImpl.getAllActiveUsers();

        // assertions
        Assertions.assertFalse(userDtoList.isEmpty());
        Assertions.assertEquals(activeUsers.get(0).getFirstName(), userDtoList.get(0).getFirstName());
    }

    @Test
    void testAcceptNewUser() {
        User user = getTestUserDetails(10);

        // mock repository
        Mockito.when(userRepository.findByUserId(Mockito.anyInt())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        //mock email service
        Mockito.doNothing().when(emailService).sendSimpleEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("userId", 10);

        // call test method
        UserDto responseDto = adminServiceImpl.acceptNewUser(requestMap);

        // assertions
        Assertions.assertEquals("Akila", responseDto.getFirstName());
    }

    @Test
    void testDeleteUser() {
        User user = getTestUserDetails(10);

        // mock repository
        Mockito.when(userRepository.findByUserId(10)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.deleteById(10)).thenReturn(Optional.of(user));

        //mock email service
        Mockito.doNothing().when(emailService).sendSimpleEmail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("userId", "10");
        requestMap.put("rejectReason", "user Removed");
        requestMap.put("email", "test@example.com");

        // call test method
        boolean isEmpty = adminServiceImpl.deleteUser(requestMap);

        // assertions
        Mockito.verify(userRepository).deleteById(10);
        Assertions.assertFalse(isEmpty);
    }

    @Test
    void testDisableUsers() {
        User user = getTestUserDetails(10);

        // mock repository
        Mockito.when(userRepository.findByUserId(10)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        Map<String, Integer> requestMap = new HashMap<>();
        requestMap.put("userId", 10);

        // call test method
        UserDto userDto = adminServiceImpl.disableUsers(requestMap);

        // assertions
        Assertions.assertEquals(10, userDto.getUserId());
    }


    User getTestUserDetails(int userId) {
        User user = new User();
        user.setUserId(userId);
        user.setFirstName("Akila");
        user.setLastName("Malshan");
        user.setEmail("test@example.com");
        return user;
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
