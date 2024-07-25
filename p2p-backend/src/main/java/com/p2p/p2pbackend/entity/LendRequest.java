package com.p2p.p2pbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lendRequests")
public class LendRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lendRequestId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double amount;
    private Double total;
    private Double interestRate;
    private Integer repaymentPeriod;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "accepted_by")
    private User acceptedBy;

    // Getters and setters
}
