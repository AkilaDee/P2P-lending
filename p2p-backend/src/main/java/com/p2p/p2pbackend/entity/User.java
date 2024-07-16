package com.p2p.p2pbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "proof_of_id")
    private String proofOfId;
    @Column(name = "proof_of_address")
    private String proofOfAddress;
    @Column(name = "financial_info")
    private String financialInfo;
    @Column(name = "credit_score")
    private String creditScore;
    @Column(name = "active_status")
    private boolean activeStatus;

    public boolean getActiveStatus() {

        return activeStatus;
    }
}
