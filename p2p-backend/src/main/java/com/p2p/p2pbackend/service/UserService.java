package com.p2p.p2pbackend.service;

import com.p2p.p2pbackend.dto.LendRequestDto;
import com.p2p.p2pbackend.dto.LoanRequestDto;
import com.p2p.p2pbackend.dto.SignInDto;
import com.p2p.p2pbackend.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(int userId);
    UserDto getUserByEmail(String email);
//    UserDto signIn(String email, String password);
    UserDto submitLoanRequestRating(Map<String, Object> requestMap);
    UserDto submitLendRequestRating(Map<String, Object> requestMap);
    LendRequestDto submitLendRequest(LendRequestDto lendRequestDto);
    LendRequestDto acceptLendRequest(Map<String, Integer> requestMap);
    Void deleteLendRequest(Map<String, Integer> requestMap);
    LendRequestDto payLendRequest(Map<String, Integer> requestMap);
    LendRequestDto payBackLendRequest(Map<String, Integer> requestMap);
    LoanRequestDto submitLoanRequest(LoanRequestDto loanRequestDto);
    LoanRequestDto acceptLoanRequest(Map<String, Integer> requestMap);
    Void deleteLoanRequest(Map<String, Integer> requestMap);
    LoanRequestDto payBackLoanRequest(Map<String, Integer> requestMap);
    ResponseEntity<String> signUp(
            @RequestPart("userDto") UserDto userDto,
            @RequestPart("proofOfIdFile") MultipartFile proofOfIdFile,
            @RequestPart("proofOfAddressFile") MultipartFile proofOfAddressFile,
            @RequestPart("financialInfoFile") MultipartFile financialInfoFile,
            @RequestPart("creditScoreFile") MultipartFile creditScoreFile);
    Map<String, Object> signIn(SignInDto signInDto);
    UserDto getUserProfile(Map<String, Integer> requestMap);
    UserDto viewUserProfile(Map<String, Integer> requestMap);
    List<Object> getAllLendRequestsExcludingCurrentUser(Map<String, Integer> requestMap);
    List<LendRequestDto> getAllPendingLendRequestsForUser(Map<String, Integer> requestMap);
    List<Object> getApprovedLendRequests(Map<String, Integer> requestMap);
    List<Object> getLendRequestsAcceptedByYou(Map<String, Integer> requestMap);
    List<Object> getAllLoanRequestsExcludingCurrentUser(Map<String, Integer> requestMap);
    List<LoanRequestDto> getAllPendingLoanRequestsForUser(Map<String, Integer> requestMap);
    List<Object> getApprovedLoanRequests(Map<String, Integer> requestMap);
    List<Object> getLoanRequestsAcceptedByYou(Map<String, Integer> requestMap);
}
