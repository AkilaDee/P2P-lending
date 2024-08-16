package com.p2p.p2pbackend.controller;

import com.p2p.p2pbackend.dto.*;
import com.p2p.p2pbackend.entity.*;
import com.p2p.p2pbackend.mapper.*;
import com.p2p.p2pbackend.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final LendRequestRepository lendRequestRepository;
    private final LoanRequestRepository loanRequestRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


//    @PostMapping("/signup")
//    public ResponseEntity<String> createUser(
//            @RequestPart("userDto") UserDto userDto,
//            @RequestPart("proofOfIdFile") MultipartFile proofOfIdFile,
//            @RequestPart("proofOfAddressFile") MultipartFile proofOfAddressFile,
//            @RequestPart("financialInfoFile") MultipartFile financialInfoFile,
//            @RequestPart("creditScoreFile") MultipartFile creditScoreFile) {
//
//        try {
//            // Convert files to byte arrays
//            byte[] proofOfId = proofOfIdFile.getBytes();
//            byte[] proofOfAddress = proofOfAddressFile.getBytes();
//            byte[] financialInfo = financialInfoFile.getBytes();
//            byte[] creditScore = creditScoreFile.getBytes();
//
//            // Create User entity
//            User user = new User();
//            user.setFirstName(userDto.getFirstName());
//            user.setLastName(userDto.getLastName());
//            user.setEmail(userDto.getEmail());
//            user.setPassword(new BCryptPasswordEncoder().encode(userDto.getPassword()));
//            user.setProofOfId(proofOfId);
//            user.setProofOfAddress(proofOfAddress);
//            user.setFinancialInfo(financialInfo);
//            user.setCreditScore(creditScore);
//
//            // Check for existing email
//            if (userRepository.existsByEmail(user.getEmail())) {
//                return ResponseEntity.status(HttpStatus.CONFLICT)
//                        .body("An account with this email already exists");
//            }
//
//            // Save user and return response
//            userRepository.save(user);
//            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
//        } catch (IOException e) {
//            logger.error("Error processing file uploads", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error processing file uploads: " + e.getMessage());
//        }
//    }


    @PostMapping("/signup")
    public ResponseEntity<String> createUser(
            @RequestPart("userDto") UserDto userDto,
            @RequestPart("proofOfIdFile") MultipartFile proofOfIdFile,
            @RequestPart("proofOfAddressFile") MultipartFile proofOfAddressFile,
            @RequestPart("financialInfoFile") MultipartFile financialInfoFile,
            @RequestPart("creditScoreFile") MultipartFile creditScoreFile) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("An account with this email already exists");
        }

        try {

            if (proofOfIdFile != null && !proofOfIdFile.isEmpty()) {
                userDto.setProofOfId(proofOfIdFile.getBytes());
            }
            if (proofOfAddressFile != null && !proofOfAddressFile.isEmpty()) {
                userDto.setProofOfAddress(proofOfAddressFile.getBytes());
            }
            if (financialInfoFile != null && !financialInfoFile.isEmpty()) {
                userDto.setFinancialInfo(financialInfoFile.getBytes());
            }
            if (creditScoreFile != null && !creditScoreFile.isEmpty()) {
                userDto.setCreditScore(creditScoreFile.getBytes());
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file uploads: " + e.getMessage());
        }

        User user = UserMapper.mapToUser(userDto);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully");
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
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if(user.getRating()==0){
            throw new RuntimeException("User is disabled");
        }

        if(!user.getActiveStatus()){
            throw new RuntimeException("User is not accepted");
        }

        UserDto userDto = UserMapper.mapToUserDto(user);
        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);

        // Set role based on email
        String role = "user";
        if ("admin@peerfund.com".equals(user.getEmail())) {
            role = "admin";
        }
        response.put("role", role);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = UserMapper.mapToUserDto(user);
        userDto.setPassword(null); // Exclude password
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/loanrequest/submitrating")
    public ResponseEntity<UserDto> submitLoanRequestRating(@RequestBody Map<String, Object> requestMap) {
        int loanRequestId = (int) requestMap.get("loanRequestId");
        int userId = (int) requestMap.get("userId");
        double submittedRating = ((Number) requestMap.get("rating")).doubleValue();

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Double currentRating = user.getRating();
        Integer currentRatingCount = user.getRatingCount();


        if (currentRatingCount == null) {
            currentRatingCount = 0;
        }

        double newRating = ((currentRating * currentRatingCount) + submittedRating) / (currentRatingCount + 1);
        user.setRating(newRating);
        user.setRatingCount(currentRatingCount + 1);

        userRepository.save(user);

        UserDto userDto = UserMapper.mapToUserDto(user);

        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new RuntimeException("Lend request not found"));

        if(userId == loanRequest.getUser().getUserId()) {
            if("CLOSED".equals(loanRequest.getStatus())) {
                loanRequest.setStatus("CLOSED/Rated by Acceptor");
            } else {
                loanRequest.setStatus("CLOSED/Rated by Both");
            }
        } else {
            if("CLOSED".equals(loanRequest.getStatus())) {
                loanRequest.setStatus("CLOSED/Rated by Requester");
            } else {
                loanRequest.setStatus("CLOSED/Rated by Both");
            }
        }

        loanRequest = loanRequestRepository.save(loanRequest);
        LoanRequestDto updatedLoanRequest = LoanRequestMapper.mapToLoanRequestDto(loanRequest);


        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/lendrequest/submitrating")
    public ResponseEntity<UserDto> submitLendRequestRating(@RequestBody Map<String, Object> requestMap) {
        int lendRequestId = (int) requestMap.get("lendRequestId");
        int userId = (int) requestMap.get("userId");
        double submittedRating = ((Number) requestMap.get("rating")).doubleValue();
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Double currentRating = user.getRating();
        Integer currentRatingCount = user.getRatingCount();


        if (currentRatingCount == null) {
            currentRatingCount = 0;
        }

        double newRating = ((currentRating * currentRatingCount) + submittedRating) / (currentRatingCount + 1);
        user.setRating(newRating);
        user.setRatingCount(currentRatingCount + 1);

        userRepository.save(user);

        UserDto userDto = UserMapper.mapToUserDto(user);

        LendRequest lendRequest = lendRequestRepository.findById(lendRequestId)
                .orElseThrow(() -> new RuntimeException("Lend request not found"));

        if(userId == lendRequest.getUser().getUserId()) {
            if("CLOSED".equals(lendRequest.getStatus())) {
                lendRequest.setStatus("CLOSED/Rated by Acceptor");
            } else {
                lendRequest.setStatus("CLOSED/Rated by Both");
            }
        } else {
            if("CLOSED".equals(lendRequest.getStatus())) {
                lendRequest.setStatus("CLOSED/Rated by Requester");
            } else {
                lendRequest.setStatus("CLOSED/Rated by Both");
            }
        }
        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto updatedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);

        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/viewuser")
    public ResponseEntity<UserDto> viewUserProfile(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = UserMapper.mapToUserDto(user);
        userDto.setPassword(null); // Exclude password
        userDto.setEmail(null);
        return ResponseEntity.ok(userDto);
    }
    // LendRequest endpoints

    @PostMapping("/lendrequests/exclude")
    public ResponseEntity<List<Object>> getAllLendRequestsExcludingCurrentUser(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        List<Object[]> results = lendRequestRepository.findByUser_UserIdNotAndStatus(userId);

        List<Object> response = results.stream().map(result -> {
            LendRequest lendRequest = (LendRequest) result[0];
            int requestorId = (int) result[1];
            String firstName = (String) result[2];
            String lastName = (String) result[3];

            return new Object() {
                public final int lendRequestId = lendRequest.getLendRequestId();
                public final int userId = lendRequest.getUser().getUserId();
                public final Double amount = lendRequest.getAmount();
                public final Double total = lendRequest.getTotal();
                public final Double interestRate = lendRequest.getInterestRate();
                public final Integer repaymentPeriod = lendRequest.getRepaymentPeriod();
                public final String status = lendRequest.getStatus();
                public final LocalDateTime createdAt = lendRequest.getCreatedAt();
                public final LocalDateTime updatedAt = lendRequest.getUpdatedAt();
                public final Integer acceptedBy = lendRequest.getAcceptedBy() != null ? lendRequest.getAcceptedBy().getUserId() : null;
                public final int requestedUserId = requestorId;
                public final String requestedByFirstName = firstName;
                public final String requestedByLastName = lastName;
            };
        }).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
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

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        lendRequest.setStatus("PENDING");
        lendRequest.setCreatedAt(now);
        lendRequest.setUpdatedAt(now);
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
        lendRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

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

    @PostMapping("/lendrequests/accepted")
    public ResponseEntity<List<Object>> getApprovedLendRequestsForUser(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        List<Object[]> results = lendRequestRepository.findApprovedLendRequestsWithApproverDetailsByUserId(userId);

        List<Object> response = results.stream().map(result -> {
            LendRequest lendRequest = (LendRequest) result[0];
            int acceptorId = (int) result[1];
            String firstName = (String) result[2];
            String lastName = (String) result[3];

            return new Object() {
                public final int lendRequestId = lendRequest.getLendRequestId();
                public final int userId = lendRequest.getUser().getUserId();
                public final Double amount = lendRequest.getAmount();
                public final Double total = lendRequest.getTotal();
                public final Double interestRate = lendRequest.getInterestRate();
                public final Integer repaymentPeriod = lendRequest.getRepaymentPeriod();
                public final String status = lendRequest.getStatus();
                public final LocalDateTime createdAt = lendRequest.getCreatedAt();
                public final LocalDateTime updatedAt = lendRequest.getUpdatedAt();
                public final Integer acceptedBy = lendRequest.getAcceptedBy() != null ? lendRequest.getAcceptedBy().getUserId() : null;
                public final int acceptedUserId = acceptorId;
                public final String acceptedByFirstName = firstName;
                public final String acceptedByLastName = lastName;
            };
        }).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/lendrequests/acceptedbyyou")
    public ResponseEntity<List<Object>> getApprovedLendRequestsAcceptedByUser(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        List<Object[]> results = lendRequestRepository.findApprovedLendRequestsAcceptedByUserWithUserDetails(userId);

        List<Object> response = results.stream().map(result -> {
            LendRequest lendRequest = (LendRequest) result[0];
            int acceptorId = (int) result[1];
            String firstName = (String) result[2];
            String lastName = (String) result[3];

            return new Object() {
                public final int lendRequestId = lendRequest.getLendRequestId();
                public final int userId = lendRequest.getUser().getUserId();
                public final Double amount = lendRequest.getAmount();
                public final Double total = lendRequest.getTotal();
                public final Double interestRate = lendRequest.getInterestRate();
                public final Integer repaymentPeriod = lendRequest.getRepaymentPeriod();
                public final String status = lendRequest.getStatus();
                public final LocalDateTime createdAt = lendRequest.getCreatedAt();
                public final LocalDateTime updatedAt = lendRequest.getUpdatedAt();
                public final int acceptedUserId = acceptorId;
                public final String userFirstName = firstName;
                public final String userLastName = lastName;
            };
        }).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/lendrequests/pay")
    public ResponseEntity<LendRequestDto> payLendRequest(@RequestBody Map<String, Integer> requestMap) {
        int lendRequestId = requestMap.get("lendRequestId");

        LendRequest lendRequest = lendRequestRepository.findById(lendRequestId)
                .orElseThrow(() -> new RuntimeException("Lend request not found"));


        lendRequest.setStatus("PAID");
        lendRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto updatedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);

        return ResponseEntity.ok(updatedLendRequest);
    }

    @PostMapping("/lendrequests/payback")
    public ResponseEntity<LendRequestDto> payBackLendRequest(@RequestBody Map<String, Integer> requestMap) {
        int lendRequestId = requestMap.get("lendRequestId");

        LendRequest lendRequest = lendRequestRepository.findById(lendRequestId)
                .orElseThrow(() -> new RuntimeException("Lend request not found"));


        lendRequest.setStatus("CLOSED");
        lendRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto updatedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);

        return ResponseEntity.ok(updatedLendRequest);
    }




    // LoanRequest endpoints
    @PostMapping("/loanrequests/exclude")
    public ResponseEntity<List<Object>> getAllLoanRequestsExcludingCurrentUser(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        List<Object[]> results = loanRequestRepository.findByUser_UserIdNotAndStatus(userId);

        List<Object> response = results.stream().map(result -> {
            LoanRequest loanRequest = (LoanRequest) result[0];
            int requestorId = (int) result[1];
            String firstName = (String) result[2];
            String lastName = (String) result[3];

            return new Object() {
                public final int lendRequestId = loanRequest.getLoanRequestId();
                public final int userId = loanRequest.getUser().getUserId();
                public final Double amount = loanRequest.getAmount();
                public final Double total = loanRequest.getTotal();
                public final Double interestRate = loanRequest.getInterestRate();
                public final Integer repaymentPeriod = loanRequest.getRepaymentPeriod();
                public final String status = loanRequest.getStatus();
                public final LocalDateTime createdAt = loanRequest.getCreatedAt();
                public final LocalDateTime updatedAt = loanRequest.getUpdatedAt();
                public final Integer acceptedBy = loanRequest.getAcceptedBy() != null ? loanRequest.getAcceptedBy().getUserId() : null;
                public final int requestedUserId = requestorId;
                public final String requestedByFirstName = firstName;
                public final String requestedByLastName = lastName;
            };
        }).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PostMapping("/loanrequests/submit")
    public ResponseEntity<LoanRequestDto> submitLoanRequest(@RequestBody LoanRequestDto loanRequestDto) {
        int userId = loanRequestDto.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        LoanRequest loanRequest = LoanRequestMapper.mapToLoanRequest(loanRequestDto, user, null);
        loanRequest.setStatus("PENDING");
        loanRequest.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        loanRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
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
        loanRequest.setStatus("PAID");
        loanRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

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

    @PostMapping("/loanrequests/accepted")
    public ResponseEntity<List<Object>> getApprovedLoanRequestsForUser(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        List<Object[]> results = loanRequestRepository.findApprovedLoanRequestsWithApproverDetailsByUserId(userId);

        List<Object> response = results.stream().map(result -> {
            LoanRequest loanRequest = (LoanRequest) result[0];
            int acceptorId = (int) result[1];
            String firstName = (String) result[2];
            String lastName = (String) result[3];

            return new Object() {
                public final int loanRequestId = loanRequest.getLoanRequestId();
                public final int userId = loanRequest.getUser().getUserId();
                public final Double amount = loanRequest.getAmount();
                public final Double total = loanRequest.getTotal();
                public final Double interestRate = loanRequest.getInterestRate();
                public final Integer repaymentPeriod = loanRequest.getRepaymentPeriod();
                public final String status = loanRequest.getStatus();
                public final LocalDateTime createdAt = loanRequest.getCreatedAt();
                public final LocalDateTime updatedAt = loanRequest.getUpdatedAt();
                public final Integer acceptedBy = loanRequest.getAcceptedBy() != null ? loanRequest.getAcceptedBy().getUserId() : null;
                public final int acceptedUserId = acceptorId;
                public final String acceptedByFirstName = firstName;
                public final String acceptedByLastName = lastName;
            };
        }).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/loanrequests/acceptedbyyou")
    public ResponseEntity<List<Object>> getApprovedLoanRequestsAcceptedByUser(@RequestBody UserIdRequest userIdRequest) {
        int userId = userIdRequest.getUserId();
        List<Object[]> results = loanRequestRepository.findApprovedLoanRequestsAcceptedByUserWithUserDetails(userId);

        List<Object> response = results.stream().map(result -> {
            LoanRequest loanRequest = (LoanRequest) result[0];
            int requestorId = (int) result[1];
            String firstName = (String) result[2];
            String lastName = (String) result[3];

            return new Object() {
                public final int loanRequestId = loanRequest.getLoanRequestId();
                public final int userId = loanRequest.getUser().getUserId();
                public final Double amount = loanRequest.getAmount();
                public final Double total = loanRequest.getTotal();
                public final Double interestRate = loanRequest.getInterestRate();
                public final Integer repaymentPeriod = loanRequest.getRepaymentPeriod();
                public final String status = loanRequest.getStatus();
                public final LocalDateTime createdAt = loanRequest.getCreatedAt();
                public final LocalDateTime updatedAt = loanRequest.getUpdatedAt();
                public final int requestedUserId = requestorId;
                public final String userFirstName = firstName;
                public final String userLastName = lastName;
            };
        }).collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/loanrequests/payback")
    public ResponseEntity<LoanRequestDto> payBackLoanRequest(@RequestBody Map<String, Integer> requestMap) {
        Logger logger = LoggerFactory.getLogger(UserController.class);

        Integer loanRequestId = requestMap.get("loanRequestId");

        if (loanRequestId == null) {
            throw new RuntimeException("loanRequestId must be provided");
        }

        logger.info("Received acceptLoanRequest with loanRequestId: {}", loanRequestId);

        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new RuntimeException("Loan request not found"));


        loanRequest.setStatus("PAID");
        loanRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        loanRequest = loanRequestRepository.save(loanRequest);
        LoanRequestDto updatedLoanRequest = LoanRequestMapper.mapToLoanRequestDto(loanRequest);

        return ResponseEntity.ok(updatedLoanRequest);
    }
}

