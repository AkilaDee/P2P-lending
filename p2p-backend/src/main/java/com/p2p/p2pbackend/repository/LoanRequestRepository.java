package com.p2p.p2pbackend.repository;

import com.p2p.p2pbackend.entity.LoanRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Integer> {
}
