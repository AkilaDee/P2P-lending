package com.p2p.p2pbackend.service.impl;

import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.entity.User;
import com.p2p.p2pbackend.mapper.UserMapper;
import com.p2p.p2pbackend.repository.UserRepository;
import com.p2p.p2pbackend.service.AdminService;
import com.p2p.p2pbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<UserDto> getAllInactiveUsers() {
        List<User> inactiveUsers = userRepository.findByActiveStatusFalse();
        List<UserDto> usersList = inactiveUsers.stream()
                .map(user -> {
                    UserDto userDto = UserMapper.mapToUserDto(user);
                    userDto.setPassword(null); // Exclude password
                    return userDto;
                })
                .collect(Collectors.toList());
        return usersList;
    }

    @Override
    public UserDto acceptNewUser(Map<String, Integer> requestMap) {
        int userId = requestMap.get("userId");
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActiveStatus(true);
        String email = user.getEmail();
        User savedUser = userRepository.save(user);

        UserDto userDto = userMapper.mapToUserDto(savedUser);
        String subject = "Account Acceptance Notification";
        String body = "Dear user, your account has been accepted!";
        emailService.sendSimpleEmail(email, subject, body);

        return userDto;
    }

    @Override
    public boolean deleteUser(Map<String, String> requestMap) {
        int userId = Integer.parseInt(requestMap.get("userId"));
        String rejectReason = requestMap.get("rejectReason");
        String email = requestMap.get("email");

        if (rejectReason == null || rejectReason.isEmpty()) {
            rejectReason = "No specific reason provided.";
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<User> deletedUser = userRepository.deleteById(userId);

        String subject = "Account Rejection Notification";
        String body = "Dear user, your account has been rejected for the following reason: " + rejectReason;
        emailService.sendSimpleEmail(email, subject, body);
        return deletedUser.isEmpty();
    }

    @Override
    public List<UserDto> getAllActiveUsers() {
        List<User> activeUsers = userRepository.findByActiveStatusTrue();
        List<UserDto> userDtos = activeUsers.stream()
                .filter(user -> !("admin@peerfund.com".equals(user.getEmail()))) // Exclude users with "admin" email
                .map(user -> {
                    UserDto userDto = UserMapper.mapToUserDto(user);
                    userDto.setPassword(null); // Exclude password
                    return userDto;
                })
                .collect(Collectors.toList());
        return userDtos;
    }

    @Override
    public UserDto disableUsers(Map<String, Integer> requestMap) {
        int userId = requestMap.get("userId");
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActiveStatus(false);
        user.setRating(0);
        User savedUser = userRepository.save(user);

        UserDto userDto = userMapper.mapToUserDto(savedUser);
        return userDto;
    }
}
