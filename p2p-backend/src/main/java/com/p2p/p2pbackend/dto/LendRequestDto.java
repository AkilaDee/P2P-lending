package com.p2p.p2pbackend.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LendRequestDto {

    private int lendRequestId;
    private int userId;
    private Double amount;
    private Double interestRate;
    private Integer repaymentPeriod;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer acceptedBy;
}