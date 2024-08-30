package com.p2p.p2pbackend.controller;

import com.p2p.p2pbackend.dto.*;
import com.p2p.p2pbackend.entity.*;
import com.p2p.p2pbackend.mapper.*;
import com.p2p.p2pbackend.repository.*;
import com.p2p.p2pbackend.service.AdminService;
import com.p2p.p2pbackend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashMap;
import java.time.temporal.ChronoUnit;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class UserController {

    private final UserRepository userRepository;
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(
            @RequestPart("userDto") UserDto userDto,
            @RequestPart("proofOfIdFile") MultipartFile proofOfIdFile,
            @RequestPart("proofOfAddressFile") MultipartFile proofOfAddressFile,
            @RequestPart("financialInfoFile") MultipartFile financialInfoFile,
            @RequestPart("creditScoreFile") MultipartFile creditScoreFile) {

        ResponseEntity<String> response = userService.signUp(  userDto, proofOfIdFile, proofOfAddressFile, financialInfoFile, creditScoreFile);
        return response;
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
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody SignInDto signInDto) {
        Map<String, Object> response=userService.signIn(signInDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(@RequestBody Map<String, Integer> requestMap) {
        UserDto userDto = userService.getUserProfile(requestMap);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/loanrequest/submitrating")
    public ResponseEntity<UserDto> submitLoanRequestRating(@RequestBody Map<String, Object> requestMap) {
        UserDto ratedUser = userService.submitLoanRequestRating(requestMap);
        return new ResponseEntity<>(ratedUser, HttpStatus.OK);
    }

    @PostMapping("/lendrequest/submitrating")
    public ResponseEntity<UserDto> submitLendRequestRating(@RequestBody Map<String, Object> requestMap) {
        UserDto ratedUser = userService.submitLendRequestRating(requestMap);
        return new ResponseEntity<>(ratedUser, HttpStatus.OK);
    }

    @PostMapping("/viewuser")
    public ResponseEntity<UserDto> viewUserProfile(@RequestBody Map<String, Integer> requestMap) {
        UserDto userDto =userService.viewUserProfile(requestMap);
        return ResponseEntity.ok(userDto);
    }

    // LendRequest endpoints

    @PostMapping("/lendrequests/exclude")
    public ResponseEntity<List<Object>> getAllLendRequestsExcludingCurrentUser(@RequestBody Map<String, Integer> requestMap) {
        List<Object> response = userService.getAllLendRequestsExcludingCurrentUser(requestMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/lendrequests/submit")
    public ResponseEntity<LendRequestDto> submitLendRequest(@RequestBody LendRequestDto lendRequestDto) {
        LendRequestDto savedLendRequest = userService.submitLendRequest(lendRequestDto);
        return new ResponseEntity<>(savedLendRequest, HttpStatus.CREATED);
    }

    @PostMapping("/lendrequests/accept")
    public ResponseEntity<LendRequestDto> acceptLendRequest(@RequestBody Map<String, Integer> requestMap) {
        LendRequestDto updatedLendRequest = userService.acceptLendRequest(requestMap);
        return ResponseEntity.ok(updatedLendRequest);
    }

    @PostMapping("/lendrequests/pending")
    public ResponseEntity<List<LendRequestDto>> getAllPendingLendRequestsForUser(@RequestBody Map<String, Integer> requestMap) {
        List<LendRequestDto> lendRequestDtos = userService.getAllPendingLendRequestsForUser(requestMap);
        return new ResponseEntity<>(lendRequestDtos, HttpStatus.OK);
    }

    @PostMapping("/lendrequests/delete")
    public ResponseEntity<Void> deleteLendRequest(@RequestBody Map<String, Integer> requestMap) {
        userService.deleteLendRequest(requestMap);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/lendrequests/accepted")
    public ResponseEntity<List<Object>> getApprovedLendRequestsForUser(@RequestBody Map<String, Integer> requestMap) {
        List<Object> response = userService.getApprovedLendRequests(requestMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/lendrequests/acceptedbyyou")
    public ResponseEntity<List<Object>> getApprovedLendRequestsAcceptedByUser(@RequestBody Map<String, Integer> requestMap) {
        List<Object> response = userService.getLendRequestsAcceptedByYou(requestMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/lendrequests/pay")
    public ResponseEntity<LendRequestDto> payLendRequest(@RequestBody Map<String, Integer> requestMap) {
        LendRequestDto updatedLendRequest = userService.payLendRequest(requestMap);
        return ResponseEntity.ok(updatedLendRequest);
    }

    @PostMapping("/lendrequests/payback")
    public ResponseEntity<LendRequestDto> payBackLendRequest(@RequestBody Map<String, Integer> requestMap) {
        LendRequestDto updatedLendRequest = userService.payBackLendRequest(requestMap);
        return ResponseEntity.ok(updatedLendRequest);
    }


    // LoanRequest endpoints
    @PostMapping("/loanrequests/exclude")
    public ResponseEntity<List<Object>> getAllLoanRequestsExcludingCurrentUser(@RequestBody Map<String, Integer> requestMap) {
        List<Object> response = userService.getAllLoanRequestsExcludingCurrentUser(requestMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/loanrequests/submit")
    public ResponseEntity<LoanRequestDto> submitLoanRequest(@RequestBody LoanRequestDto loanRequestDto) {
        LoanRequestDto savedLoanRequest =userService.submitLoanRequest(loanRequestDto);
        return new ResponseEntity<>(savedLoanRequest, HttpStatus.CREATED);
    }

    @PostMapping("/loanrequests/accept")
    public ResponseEntity<LoanRequestDto> acceptLoanRequest(@RequestBody Map<String, Integer> requestMap) {
        LoanRequestDto updatedLoanRequest = userService.acceptLoanRequest(requestMap);
        return ResponseEntity.ok(updatedLoanRequest);
    }

    @PostMapping("/loanrequests/pending")
    public ResponseEntity<List<LoanRequestDto>> getAllPendingLoanRequestsForUser(@RequestBody Map<String, Integer> requestMap) {
        List<LoanRequestDto> loanRequestDtos = userService.getAllPendingLoanRequestsForUser(requestMap);
        return new ResponseEntity<>(loanRequestDtos, HttpStatus.OK);
    }

    @PostMapping("/loanrequests/delete")
    public ResponseEntity<Void> deleteLoanRequest(@RequestBody Map<String, Integer> requestMap) {
        userService.deleteLoanRequest(requestMap);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/loanrequests/accepted")
    public ResponseEntity<List<Object>> getApprovedLoanRequestsForUser(@RequestBody Map<String, Integer> requestMap) {
        List<Object> response = userService.getApprovedLoanRequests(requestMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/loanrequests/acceptedbyyou")
    public ResponseEntity<List<Object>> getApprovedLoanRequestsAcceptedByUser(@RequestBody Map<String, Integer> requestMap) {
        List<Object> response = userService.getLoanRequestsAcceptedByYou(requestMap);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/loanrequests/payback")
    public ResponseEntity<LoanRequestDto> payBackLoanRequest(@RequestBody Map<String, Integer> requestMap) {
        LoanRequestDto updatedLoanRequest = userService.payBackLoanRequest(requestMap);
        return ResponseEntity.ok(updatedLoanRequest);
    }
}

