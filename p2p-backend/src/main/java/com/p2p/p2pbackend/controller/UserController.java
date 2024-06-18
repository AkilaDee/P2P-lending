package com.p2p.p2pbackend.controller;

import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.dto.SignInDto;
import com.p2p.p2pbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        UserDto savedUser = userService.createUser(userDto);

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("userId") int userId){
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam("email") String email) {
        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/signin")
    public ResponseEntity<UserDto> signIn(@RequestBody SignInDto signInDto) {
        UserDto userDto = userService.signIn(signInDto.getEmail(), signInDto.getPassword());
        return ResponseEntity.ok(userDto);
    }
}
