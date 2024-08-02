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

    @Lob
    @Column(name = "proof_of_id")
    private byte[] proofOfId;

    @Lob
    @Column(name = "proof_of_address")
    private byte[] proofOfAddress;

    @Lob
    @Column(name = "financial_info")
    private byte[] financialInfo;

    @Lob
    @Column(name = "credit_score")
    private byte[] creditScore;

    @Column(name = "active_status")
    private boolean activeStatus = false;

    @Column(name = "rating")
    private double rating = 5.0;

    @Column(name = "rating_count")
    private Integer ratingCount = 0;

    public boolean getActiveStatus() {
        return activeStatus;
    }
}
