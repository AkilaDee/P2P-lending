package com.p2p.p2pbackend.repository;

import com.p2p.p2pbackend.entity.LendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LendRequestRepository extends JpaRepository<LendRequest, Integer> {
}
