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
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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

        // Encrypt the password using BCryptPasswordEncoder
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

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

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @PostMapping("/signin")
    public ResponseEntity<UserDto> signIn(@RequestBody SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        UserDto userDto = UserMapper.mapToUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    // LendRequest endpoints

    @PostMapping("/lendrequests/exclude")
    public ResponseEntity<List<LendRequestDto>> getAllLendRequestsExcludingCurrentUser(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        List<LendRequest> lendRequests = lendRequestRepository.findByUser_UserIdNotAndStatus(userId, "PENDING");

        List<LendRequestDto> lendRequestDtos = lendRequests.stream()
                .map(LendRequestMapper::mapToLendRequestDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(lendRequestDtos, HttpStatus.OK);
    }

    public static class UserIdRequest {
        private int userId;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }

    @PostMapping("/lendrequests/submit")
    public ResponseEntity<LendRequestDto> submitLendRequest(@RequestBody LendRequestDto lendRequestDto) {
        int userId = lendRequestDto.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        LendRequest lendRequest = LendRequestMapper.mapToLendRequest(lendRequestDto, user, null);
        lendRequest.setStatus("PENDING");
        lendRequest.setCreatedAt(LocalDateTime.now());
        lendRequest.setUpdatedAt(LocalDateTime.now());
        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto savedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);
        return new ResponseEntity<>(savedLendRequest, HttpStatus.CREATED);
    }

    @PostMapping("/lendrequests/accept")
    public ResponseEntity<LendRequestDto> acceptLendRequest(@RequestBody Map<String, Integer> requestMap) {
        int lendRequestId = requestMap.get("lendRequestId");
        int acceptorId = requestMap.get("acceptorId");

        LendRequest lendRequest = lendRequestRepository.findById(lendRequestId)
                .orElseThrow(() -> new RuntimeException("Lend request not found"));

        User acceptor = userRepository.findById(acceptorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        lendRequest.setAcceptedBy(acceptor);
        lendRequest.setStatus("APPROVED");
        lendRequest.setUpdatedAt(LocalDateTime.now());

        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto updatedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);

        return ResponseEntity.ok(updatedLendRequest);
    }

    @PostMapping("/lendrequests/pending")
    public ResponseEntity<List<LendRequestDto>> getAllpendingLendRequestsForUser(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        List<LendRequest> lendRequests = lendRequestRepository.findByUser_UserIdAndStatus(userId, "PENDING");

        List<LendRequestDto> lendRequestDtos = lendRequests.stream()
                .map(LendRequestMapper::mapToLendRequestDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(lendRequestDtos, HttpStatus.OK);
    }

    @PostMapping("/lendrequests/delete")
    public ResponseEntity<Void> deleteLendRequest(@RequestBody Map<String, Integer> requestMap) {
        int lendRequestId = requestMap.get("lendRequestId");

        LendRequest lendRequest = lendRequestRepository.findById(lendRequestId)
                .orElseThrow(() -> new RuntimeException("Lend Request not found"));

        if (!"PENDING".equals(lendRequest.getStatus())) {
            throw new RuntimeException("Only pending requests can be deleted");
        }

        lendRequestRepository.deleteById(lendRequestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }







    // LoanRequest endpoints
    @PostMapping("/loanrequests/exclude")
    public ResponseEntity<List<LoanRequestDto>> getAllLoanRequestsExcludingCurrentUser(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        List<LoanRequest> loanRequests = loanRequestRepository.findByUser_UserIdNotAndStatus(userId, "PENDING");

        List<LoanRequestDto> loanRequestDtos = loanRequests.stream()
                .map(LoanRequestMapper::mapToLoanRequestDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(loanRequestDtos, HttpStatus.OK);
    }


    @PostMapping("/loanrequests/submit")
    public ResponseEntity<LoanRequestDto> submitLoanRequest(@RequestBody LoanRequestDto loanRequestDto) {
        int userId = loanRequestDto.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        LoanRequest loanRequest = LoanRequestMapper.mapToLoanRequest(loanRequestDto, user, null);
        loanRequest.setStatus("PENDING");
        loanRequest.setCreatedAt(LocalDateTime.now());
        loanRequest.setUpdatedAt(LocalDateTime.now());
        loanRequest = loanRequestRepository.save(loanRequest);
        LoanRequestDto savedLoanRequest = LoanRequestMapper.mapToLoanRequestDto(loanRequest);
        return new ResponseEntity<>(savedLoanRequest, HttpStatus.CREATED);
    }

    @PostMapping("/loanrequests/accept")
    public ResponseEntity<LoanRequestDto> acceptLoanRequest(@RequestBody Map<String, Integer> requestMap) {
        Logger logger = LoggerFactory.getLogger(UserController.class);

        Integer loanRequestId = requestMap.get("loanRequestId");
        Integer acceptorId = requestMap.get("acceptorId");

        if (loanRequestId == null || acceptorId == null) {
            throw new RuntimeException("loanRequestId and acceptorId must be provided");
        }

        logger.info("Received acceptLoanRequest with loanRequestId: {} and acceptorId: {}", loanRequestId, acceptorId);

        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new RuntimeException("Loan request not found"));

        User acceptor = userRepository.findById(acceptorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        loanRequest.setAcceptedBy(acceptor);
        loanRequest.setStatus("APPROVED");
        loanRequest.setUpdatedAt(LocalDateTime.now());

        loanRequest = loanRequestRepository.save(loanRequest);
        LoanRequestDto updatedLoanRequest = LoanRequestMapper.mapToLoanRequestDto(loanRequest);

        return ResponseEntity.ok(updatedLoanRequest);
    }

    @PostMapping("/loanrequests/pending")
    public ResponseEntity<List<LoanRequestDto>> getAllPendingLoanRequestsForUser(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        List<LoanRequest> loanRequests = loanRequestRepository.findByUser_UserIdAndStatus(userId, "PENDING");

        List<LoanRequestDto> loanRequestDtos = loanRequests.stream()
                .map(LoanRequestMapper::mapToLoanRequestDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(loanRequestDtos, HttpStatus.OK);
    }

    @PostMapping("/loanrequests/delete")
    public ResponseEntity<Void> deleteLoanRequest(@RequestBody Map<String, Integer> requestMap) {
        Integer loanRequestId = requestMap.get("loanRequestId");
        if (loanRequestId == null) {
            throw new RuntimeException("loanRequestId is required");
        }

        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new RuntimeException("Loan Request not found"));

        if (!"PENDING".equals(loanRequest.getStatus())) {
            throw new RuntimeException("Only pending requests can be deleted");
        }

        loanRequestRepository.deleteById(loanRequestId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }





}

