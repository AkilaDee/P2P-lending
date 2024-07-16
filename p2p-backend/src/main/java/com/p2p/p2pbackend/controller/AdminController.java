package com.p2p.p2pbackend.controller;

import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.entity.User;
import com.p2p.p2pbackend.mapper.UserMapper;
import com.p2p.p2pbackend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/users/inactive")
    public ResponseEntity<List<UserDto>> getAllInactiveUsers() {
        List<User> inactiveUsers = userRepository.findByActiveStatusFalse();
        List<UserDto> userDtos = inactiveUsers.stream()
                .map(user -> {
                    UserDto userDto = UserMapper.mapToUserDto(user);
                    userDto.setPassword(null); // Exclude password
                    return userDto;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }

    @PostMapping("users/accept")
    public ResponseEntity<UserDto> acceptNewUser(@RequestBody Map<String, Integer> requestMap){
           int userId = requestMap.get("userId");
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setActiveStatus(true);

        User savedUser = userRepository.save(user);

        UserDto userDto = userMapper.mapToUserDto(savedUser);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/users/active")
    public ResponseEntity<List<UserDto>> getAllActiveUsers() {
        List<User> activeUsers = userRepository.findByActiveStatusTrue();
        List<UserDto> userDtos = activeUsers.stream()
                .map(user -> {
                    UserDto userDto = UserMapper.mapToUserDto(user);
                    userDto.setPassword(null); // Exclude password
                    return userDto;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(userDtos, HttpStatus.OK);
    }
}
