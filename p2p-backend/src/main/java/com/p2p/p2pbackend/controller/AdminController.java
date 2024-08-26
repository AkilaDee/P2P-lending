package com.p2p.p2pbackend.controller;

import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.mapper.UserMapper;
import com.p2p.p2pbackend.repository.UserRepository;
import com.p2p.p2pbackend.service.AdminService;
import com.p2p.p2pbackend.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/users/inactive")
    public ResponseEntity<List<UserDto>> getAllInactiveUsers() {
        List<UserDto> usersList = adminService.getAllInactiveUsers();
        return new ResponseEntity<>(usersList, HttpStatus.OK);
    }

    @PostMapping("users/accept")
    public ResponseEntity<UserDto> acceptNewUser(@RequestBody Map<String, Integer> requestMap) {
        UserDto userDto = adminService.acceptNewUser(requestMap);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }


    @PostMapping("users/delete")
    public ResponseEntity<UserDto> deleteUser(@RequestBody Map<String, String> requestMap) {
        boolean isDeleted = adminService.deleteUser(requestMap);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/users/active")
    public ResponseEntity<List<UserDto>> getAllActiveUsers() {
        List<UserDto> allUsers = adminService.getAllActiveUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }


    @PostMapping("/users/disable")
    public ResponseEntity<UserDto> disableUser(@RequestBody Map<String, Integer> requestMap) {
        try {
            UserDto disabledUser = adminService.disableUsers(requestMap);
            return new ResponseEntity<>(disabledUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
