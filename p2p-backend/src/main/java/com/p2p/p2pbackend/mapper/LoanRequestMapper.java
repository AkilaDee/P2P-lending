package com.p2p.p2pbackend.mapper;

import com.p2p.p2pbackend.dto.LoanRequestDto;
import com.p2p.p2pbackend.entity.LoanRequest;
import com.p2p.p2pbackend.entity.User;

public class LoanRequestMapper {

    public static LoanRequestDto mapToLoanRequestDto(LoanRequest loanRequest) {
        return new LoanRequestDto(
                loanRequest.getLoanRequestId(),
                loanRequest.getUser().getUserId(),
                loanRequest.getAmount(),
                loanRequest.getTotal(),
                loanRequest.getInterestRate(),
                loanRequest.getRepaymentPeriod(),
                loanRequest.getStatus(),
                loanRequest.getCreatedAt(),
                loanRequest.getUpdatedAt(),
                loanRequest.getAcceptedBy() != null ? loanRequest.getAcceptedBy().getUserId() : null
        );
    }

    public static LoanRequest mapToLoanRequest(LoanRequestDto loanRequestDto, User user, User acceptedBy) {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanRequestId(loanRequestDto.getLoanRequestId());
        loanRequest.setUser(user);
        loanRequest.setAmount(loanRequestDto.getAmount());
        loanRequest.setTotal(loanRequestDto.getTotal());
        loanRequest.setInterestRate(loanRequestDto.getInterestRate());
        loanRequest.setRepaymentPeriod(loanRequestDto.getRepaymentPeriod());
        loanRequest.setStatus(loanRequestDto.getStatus());
        loanRequest.setCreatedAt(loanRequestDto.getCreatedAt());
        loanRequest.setUpdatedAt(loanRequestDto.getUpdatedAt());
        loanRequest.setAcceptedBy(acceptedBy);
        return loanRequest;
    }
}
