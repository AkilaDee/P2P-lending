package com.p2p.p2pbackend.service;

import com.p2p.p2pbackend.dto.LendRequestDto;
import com.p2p.p2pbackend.dto.LoanRequestDto;
import com.p2p.p2pbackend.dto.UserDto;

import java.util.Map;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(int userId);
    UserDto getUserByEmail(String email);
    UserDto signIn(String email, String password);
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
}
