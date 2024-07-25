package com.p2p.p2pbackend.mapper;

import com.p2p.p2pbackend.dto.LendRequestDto;
import com.p2p.p2pbackend.entity.LendRequest;
import com.p2p.p2pbackend.entity.User;

public class LendRequestMapper {

    public static LendRequestDto mapToLendRequestDto(LendRequest lendRequest) {
        return new LendRequestDto(
                lendRequest.getLendRequestId(),
                lendRequest.getUser().getUserId(),
                lendRequest.getAmount(),
                lendRequest.getTotal(),
                lendRequest.getInterestRate(),
                lendRequest.getRepaymentPeriod(),
                lendRequest.getStatus(),
                lendRequest.getCreatedAt(),
                lendRequest.getUpdatedAt(),
                lendRequest.getAcceptedBy() != null ? lendRequest.getAcceptedBy().getUserId() : null
        );
    }

    public static LendRequest mapToLendRequest(LendRequestDto lendRequestDto, User user, User acceptedBy) {
        LendRequest lendRequest = new LendRequest();
        lendRequest.setLendRequestId(lendRequestDto.getLendRequestId());
        lendRequest.setUser(user);
        lendRequest.setAmount(lendRequestDto.getAmount());
        lendRequest.setTotal(lendRequestDto.getTotal());
        lendRequest.setInterestRate(lendRequestDto.getInterestRate());
        lendRequest.setRepaymentPeriod(lendRequestDto.getRepaymentPeriod());
        lendRequest.setStatus(lendRequestDto.getStatus());
        lendRequest.setCreatedAt(lendRequestDto.getCreatedAt());
        lendRequest.setUpdatedAt(lendRequestDto.getUpdatedAt());
        lendRequest.setAcceptedBy(acceptedBy);
        return lendRequest;
    }
}
