package com.p2p.p2pbackend.service.impl;

import com.p2p.p2pbackend.controller.UserController;
import com.p2p.p2pbackend.dto.LendRequestDto;
import com.p2p.p2pbackend.dto.LoanRequestDto;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

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
    public UserDto signIn(String email, String password) {
        // Find user by email or throw an exception if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist with email: " + email));

        // Check if the provided password matches the stored hashed password
        if (passwordEncoder.matches(password, user.getPassword())) {
            // Map entity to DTO and return
            return UserMapper.mapToUserDto(user);
        } else {
            // Throw an exception if the password is invalid
            throw new IllegalArgumentException("Invalid email or password");
        }
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


        loanRequest.setStatus("PAID");
        loanRequest.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        loanRequest = loanRequestRepository.save(loanRequest);
        LoanRequestDto updatedLoanRequest = LoanRequestMapper.mapToLoanRequestDto(loanRequest);
        return updatedLoanRequest;
    }
}
