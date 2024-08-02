package com.p2p.p2pbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private byte[] proofOfId;        // Changed to byte[]
    private byte[] proofOfAddress;   // Changed to byte[]
    private byte[] financialInfo;    // Changed to byte[]
    private byte[] creditScore;      // Changed to byte[]
    private boolean activeStatus;
    private double rating;
    private int ratingCount;

    public boolean getActiveStatus() {
        return activeStatus;
    }
}
