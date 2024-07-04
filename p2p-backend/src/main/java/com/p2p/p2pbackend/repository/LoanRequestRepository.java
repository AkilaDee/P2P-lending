package com.p2p.p2pbackend.repository;

import com.p2p.p2pbackend.entity.LoanRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Integer> {
    List<LoanRequest> findByUser_UserIdNotAndStatus(int userId, String status);
}