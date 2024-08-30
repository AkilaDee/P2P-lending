package com.p2p.p2pbackend.service.impl;

import com.p2p.p2pbackend.controller.UserController;
import com.p2p.p2pbackend.dto.LendRequestDto;
import com.p2p.p2pbackend.dto.LoanRequestDto;
import com.p2p.p2pbackend.dto.SignInDto;
import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.entity.LoanRequest;
import com.p2p.p2pbackend.entity.LendRequest;
import com.p2p.p2pbackend.entity.User;
import com.p2p.p2pbackend.exception.ResourceNotFoundException;
import com.p2p.p2pbackend.mapper.LendRequestMapper;
import com.p2p.p2pbackend.mapper.LoanRequestMapper;
import com.p2p.p2pbackend.mapper.UserMapper;
import com.p2p.p2pbackend.repository.LendRequestRepository;
import com.p2p.p2pbackend.repository.LoanRequestRepository;
import com.p2p.p2pbackend.repository.UserRepository;
import com.p2p.p2pbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    LoanRequestRepository loanRequestRepository;
    LendRequestRepository lendRequestRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        // Map DTO to entity
        User user = UserMapper.mapToUser(userDto);

        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user entity
        User savedUser = userRepository.save(user);

        // Map entity to DTO and return
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(int userId) {
        // Find user by ID or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist: " + userId));

        // Map entity to DTO and return
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        // Find user by email or throw an exception if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist with email: " + email));

        // Map entity to DTO and return
        return UserMapper.mapToUserDto(user);
    }


    @Override
    public UserDto submitLoanRequestRating(Map<String, Object> requestMap) {
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
        return userDto;
    }

    @Override
    public UserDto submitLendRequestRating(Map<String, Object> requestMap) {
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
        return null;
    }

    @Override
    public LendRequestDto submitLendRequest(LendRequestDto lendRequestDto) {
        int userId = lendRequestDto.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        LendRequest lendRequest = LendRequestMapper.mapToLendRequest(lendRequestDto, user, null);

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        lendRequest.setStatus("PENDING");
        lendRequest.setCreatedAt(now);
        lendRequest.setUpdatedAt(now);
        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto savedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);
        return savedLendRequest;
    }

    @Override
    public LendRequestDto acceptLendRequest(Map<String, Integer> requestMap) {
        int lendRequestId = (int) requestMap.get("lendRequestId");
        int acceptorId = (int) requestMap.get("acceptorId");

        LendRequest lendRequest = lendRequestRepository.findById(lendRequestId)
                .orElseThrow(() -> new RuntimeException("Lend request not found"));

        User acceptor = userRepository.findById(acceptorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        lendRequest.setAcceptedBy(acceptor);
        lendRequest.setStatus("APPROVED");
        lendRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto updatedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);
        return updatedLendRequest;
    }

    @Override
    public Void deleteLendRequest(Map<String, Integer> requestMap) {
        int lendRequestId = requestMap.get("lendRequestId");

        LendRequest lendRequest = lendRequestRepository.findById(lendRequestId)
                .orElseThrow(() -> new RuntimeException("Lend Request not found"));

        if (!"PENDING".equals(lendRequest.getStatus())) {
            throw new RuntimeException("Only pending requests can be deleted");
        }

        lendRequestRepository.deleteById(lendRequestId);
        return null;
    }

    @Override
    public LendRequestDto payLendRequest(Map<String, Integer> requestMap) {
        if (requestMap == null) {
            throw new IllegalArgumentException("Request map cannot be null.");
        }
        int lendRequestId = requestMap.get("lendRequestId");

        LendRequest lendRequest = lendRequestRepository.findById(lendRequestId)
                .orElseThrow(() -> new RuntimeException("Lend request not found"));


        lendRequest.setStatus("PAID");
        lendRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto updatedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);
        return updatedLendRequest;
    }

    @Override
    public LendRequestDto payBackLendRequest(Map<String, Integer> requestMap) {
        int lendRequestId = requestMap.get("lendRequestId");

        LendRequest lendRequest = lendRequestRepository.findById(lendRequestId)
                .orElseThrow(() -> new RuntimeException("Lend request not found"));


        lendRequest.setStatus("CLOSED");
        lendRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        lendRequest = lendRequestRepository.save(lendRequest);
        LendRequestDto updatedLendRequest = LendRequestMapper.mapToLendRequestDto(lendRequest);

        return updatedLendRequest;
    }

    @Override
    public LoanRequestDto submitLoanRequest(LoanRequestDto loanRequestDto) {
        int userId = loanRequestDto.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        LoanRequest loanRequest = LoanRequestMapper.mapToLoanRequest(loanRequestDto, user, null);
        loanRequest.setStatus("PENDING");
        loanRequest.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        loanRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        loanRequest = loanRequestRepository.save(loanRequest);
        LoanRequestDto savedLoanRequest = LoanRequestMapper.mapToLoanRequestDto(loanRequest);
        return savedLoanRequest;
    }

    @Override
    public LoanRequestDto acceptLoanRequest(Map<String, Integer> requestMap) {
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
        return updatedLoanRequest;
    }

    @Override
    public Void deleteLoanRequest(Map<String, Integer> requestMap) {
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
        return null;
    }

    @Override
    public LoanRequestDto payBackLoanRequest(Map<String, Integer> requestMap) {
        Logger logger = LoggerFactory.getLogger(UserController.class);

        Integer loanRequestId = requestMap.get("loanRequestId");

        if (loanRequestId == null) {
            throw new RuntimeException("loanRequestId must be provided");
        }

        logger.info("Received acceptLoanRequest with loanRequestId: {}", loanRequestId);

        LoanRequest loanRequest = loanRequestRepository.findById(loanRequestId)
                .orElseThrow(() -> new RuntimeException("Loan request not found"));


        loanRequest.setStatus("CLOSED");
        loanRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        loanRequest = loanRequestRepository.save(loanRequest);
        LoanRequestDto updatedLoanRequest = LoanRequestMapper.mapToLoanRequestDto(loanRequest);
        return updatedLoanRequest;
    }

    @Override
    public ResponseEntity<String> signUp(UserDto userDto, MultipartFile proofOfIdFile, MultipartFile proofOfAddressFile, MultipartFile financialInfoFile, MultipartFile creditScoreFile) {
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
        user.setRating(5.0);

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully");
    }

    @Override
    public Map<String, Object> signIn(SignInDto signInDto) {
        User user = userRepository.findByEmail(signInDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (user.getRating() == 0) {
            throw new RuntimeException("User is disabled");
        }

        if (!user.getActiveStatus()) {
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

        return response;
    }

    @Override
    public UserDto getUserProfile(Map<String, Integer> requestMap) {
        int userId = requestMap.getOrDefault("userId", -1);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = UserMapper.mapToUserDto(user);
        userDto.setPassword(null);
        return userDto;
    }

    @Override
    public UserDto viewUserProfile(Map<String, Integer> requestMap) {
        int userId = requestMap.getOrDefault("userId", -1);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = UserMapper.mapToUserDto(user);
        userDto.setPassword(null);
        userDto.setEmail(null);
        return userDto;
    }

    @Override
    public List<Object> getAllLendRequestsExcludingCurrentUser(Map<String, Integer> requestMap) {
        int userId = requestMap.getOrDefault("userId", -1);

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

        return response;
    }

    @Override
    public List<LendRequestDto> getAllPendingLendRequestsForUser(Map<String, Integer> requestMap) {
        int userId = requestMap.getOrDefault("userId", -1);
        List<LendRequest> lendRequests = lendRequestRepository.findByUser_UserIdAndStatus(userId, "PENDING");

        List<LendRequestDto> lendRequestDtos = lendRequests.stream()
                .map(LendRequestMapper::mapToLendRequestDto)
                .collect(Collectors.toList());
        return lendRequestDtos;
    }

    @Override
    public List<Object> getApprovedLendRequests(Map<String, Integer> requestMap) {
        int userId = requestMap.getOrDefault("userId", -1);
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

        return response;
    }

    @Override
    public List<Object> getLendRequestsAcceptedByYou(Map<String, Integer> requestMap) {
        int userId = requestMap.getOrDefault("userId", -1);
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
        return response;
    }

    @Override
    public List<Object> getAllLoanRequestsExcludingCurrentUser(Map<String, Integer> requestMap) {
        int userId = requestMap.getOrDefault("userId", -1);
        List<Object[]> results = loanRequestRepository.findByUser_UserIdNotAndStatus(userId);

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
                public final Integer acceptedBy = loanRequest.getAcceptedBy() != null ? loanRequest.getAcceptedBy().getUserId() : null;
                public final int requestedUserId = requestorId;
                public final String requestedByFirstName = firstName;
                public final String requestedByLastName = lastName;
            };
        }).collect(Collectors.toList());
        return response;
    }

    @Override
    public List<LoanRequestDto> getAllPendingLoanRequestsForUser(Map<String, Integer> requestMap) {
        int userId = requestMap.getOrDefault("userId", -1);
        List<LoanRequest> loanRequests = loanRequestRepository.findByUser_UserIdAndStatus(userId, "PENDING");

        List<LoanRequestDto> loanRequestDtos = loanRequests.stream()
                .map(LoanRequestMapper::mapToLoanRequestDto)
                .collect(Collectors.toList());

        return loanRequestDtos;
    }

    @Override
    public List<Object> getApprovedLoanRequests(Map<String, Integer> requestMap) {
        int userId = requestMap.getOrDefault("userId", -1);
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
        return response;
    }

    @Override
    public List<Object> getLoanRequestsAcceptedByYou(Map<String, Integer> requestMap) {
        int userId = requestMap.getOrDefault("userId", -1);
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
        return response;
    }
}
