package com.p2p.p2pbackend.repository;
import org.springframework.data.jpa.repository.Query;
import com.p2p.p2pbackend.entity.LoanRequest;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRequestRepository extends JpaRepository<LoanRequest, Integer> {
    List<LoanRequest> findByUser_UserIdNotAndStatus(int userId, String status);
    List<LoanRequest> findByUser_UserIdAndStatus(int userId, String status);

    @Query("SELECT lr, u.firstName, u.lastName FROM LoanRequest lr JOIN lr.acceptedBy u WHERE lr.user.userId = :userId AND lr.status = 'APPROVED'")
    List<Object[]> findApprovedLoanRequestsWithApproverDetailsByUserId(@Param("userId") int userId);
//    List<LoanRequest> findByStatusAndApprovedBy_UserId(String status, int acceptedByUserId);

    @Query("SELECT lr, u.firstName, u.lastName FROM LoanRequest lr JOIN lr.user u WHERE lr.acceptedBy.userId = :userId AND lr.status = 'APPROVED'")
    List<Object[]> findApprovedLoanRequestsAcceptedByUserWithUserDetails(@Param("userId") int userId);
}