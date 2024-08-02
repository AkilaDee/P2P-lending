package com.p2p.p2pbackend.repository;

import com.p2p.p2pbackend.entity.LendRequest;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LendRequestRepository extends JpaRepository<LendRequest, Integer> {

    @Query("SELECT lr, u.userId, u.firstName, u.lastName FROM LendRequest lr JOIN lr.user u WHERE lr.user.userId != :userId AND lr.status = 'PENDING'")
    List<Object[]> findByUser_UserIdNotAndStatus(@Param("userId") int userId);
    List<LendRequest> findByUser_UserIdAndStatus(int userId, String status);

    @Query("SELECT lr, u.userId, u.firstName, u.lastName FROM LendRequest lr JOIN lr.acceptedBy u WHERE lr.user.userId = :userId AND lr.status != 'PENDING'")
    List<Object[]> findApprovedLendRequestsWithApproverDetailsByUserId(@Param("userId") int userId);

    @Query("SELECT lr, u.userId, u.firstName, u.lastName FROM LendRequest lr JOIN lr.user u WHERE lr.acceptedBy.userId = :userId AND lr.status != 'PENDING'")
    List<Object[]> findApprovedLendRequestsAcceptedByUserWithUserDetails(@Param("userId") int userId);
}