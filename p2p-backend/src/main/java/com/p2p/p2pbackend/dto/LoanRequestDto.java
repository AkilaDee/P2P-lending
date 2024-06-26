package com.p2p.p2pbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDto {

    private int loanRequestId;
    private int userId;
    private Double amount;
    private Double interestRate;
    private Integer repaymentPeriod;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer acceptedBy;
}


