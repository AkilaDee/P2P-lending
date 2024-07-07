package com.p2p.p2pbackend.repository;

import com.p2p.p2pbackend.entity.LendRequest;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LendRequestRepository extends JpaRepository<LendRequest, Integer> {
    List<LendRequest> findByUser_UserIdNotAndStatus(int userId, String status);
    List<LendRequest> findByUser_UserIdAndStatus(int userId, String status);
}