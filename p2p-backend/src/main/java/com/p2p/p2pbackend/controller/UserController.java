package com.p2p.p2pbackend.controller;

import com.p2p.p2pbackend.dto.*;
import com.p2p.p2pbackend.entity.*;
import com.p2p.p2pbackend.mapper.*;
import com.p2p.p2pbackend.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserRepository userRepository;
    private final LendRequestRepository lendRequestRepository;
    private final LoanRequestRepository loanRequestRepository;

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        User user = UserMapper.mapToUser(userDto);
        user = userRepository.save(user);
        UserDto savedUser = UserMapper.mapToUserDto(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = UserMapper.mapToUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam("email") String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = UserMapper.mapToUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/signin")
    public ResponseEntity<UserDto> signIn(@RequestBody SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getPassword().equals(signInDto.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        UserDto userDto = UserMapper.mapToUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    // LendRequest endpoints
    @PostMapping("/{userId}/lendrequests")
    public ResponseEntity<LendRequestDto> submitLendRequest(@PathVariable int userId, @RequestBody LendRequestDto lendRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        LendRequest lendRequest = LendRequestMapper.mapToLendRequest(lendRequestDto, user, null);
        lendRequest.setStatus("PENDING");
        lendRequest.setCreatedAt(LocalDateTime.now());
        lendRequest.setUpdatedAt(LocalDateTime.now());
        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto savedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);
        return new ResponseEntity<>(savedLendRequest, HttpStatus.CREATED);
    }

    @PutMapping("/lendrequests/{lendRequestId}/accept/{acceptorId}")
    public ResponseEntity<LendRequestDto> acceptLendRequest(@PathVariable int lendRequestId, @PathVariable int acceptorId) {
        LendRequest lendRequest = lendRequestRepository.findById(lendRequestId).orElseThrow(() -> new RuntimeException("Lend request not found"));
        User acceptor = userRepository.findById(acceptorId).orElseThrow(() -> new RuntimeException("User not found"));
        lendRequest.setAcceptedBy(acceptor);
        lendRequest.setStatus("APPROVED");
        lendRequest.setUpdatedAt(LocalDateTime.now());
        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto updatedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);
        return ResponseEntity.ok(updatedLendRequest);
    }

    // LoanRequest endpoints
    @PostMapping("/{userId}/loanrequests")
    public ResponseEntity<LoanRequestDto> submitLoanRequest(@PathVariable int userId, @RequestBody LoanRequestDto loanRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        LoanRequest loanRequest = LoanRequestMapper.mapToLoanRequest(loanRequestDto, user, null);
        loanRequest.setStatus("PENDING");
        loanRequest.setCreatedAt(LocalDateTime.now());
        loanRequest.setUpdatedAt(LocalDateTime.now());
        loanRequest = loanRequestRepository.save(loanRequest);
        LoanRequestDto savedLoanRequest = LoanRequestMapper.mapToLoanRequestDto(loanRequest);
        return new ResponseEntity<>(savedLoanRequest, HttpStatus.CREATED);
    }

    @PutMapping("/loanrequests/{loanRequestId}/accept/{acceptorId}")
    public ResponseEntity<LoanRequestDto> acceptLoanRequest(@PathVariable int loanRequestId, @PathVariable int acceptorId) {
        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId).orElseThrow(() -> new RuntimeException("Loan request not found"));
        User acceptor = userRepository.findById(acceptorId).orElseThrow(() -> new RuntimeException("User not found"));
        loanRequest.setAcceptedBy(acceptor);
        loanRequest.setStatus("APPROVED");
        loanRequest.setUpdatedAt(LocalDateTime.now());
        loanRequest = loanRequestRepository.save(loanRequest);
        LoanRequestDto updatedLoanRequest = LoanRequestMapper.mapToLoanRequestDto(loanRequest);
        return ResponseEntity.ok(updatedLoanRequest);
    }
}
