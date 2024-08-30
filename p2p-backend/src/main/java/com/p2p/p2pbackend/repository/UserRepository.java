package com.p2p.p2pbackend.repository;

import com.p2p.p2pbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByActiveStatusFalse();
    Optional<User> findByUserId(int userId);
    List<User> findByActiveStatusTrue();
    Optional<User> deleteById(int userId);

    @Modifying
    @Query(value
            = "UPDATE users SET credit_score = ?1 WHERE id = ?2", nativeQuery = true)
    void updateCreditScore(byte[] creditScore, Integer userId);

    @Modifying
    @Query(value = "UPDATE users SET proof_of_id = ?1 WHERE id = ?2", nativeQuery = true)
    void updateProofOfId(byte[] proofOfId, Integer userId);

    @Modifying
    @Query(value = "UPDATE users SET proof_of_address = ?1 WHERE id = ?2", nativeQuery = true)
    void updateProofOfAddress(byte[] proofOfAddress, Integer userId);

    @Modifying
    @Query(value = "UPDATE users SET financial_info = ?1 WHERE id = ?2", nativeQuery = true)
    void updateFinancialInfo(byte[] financialInfo, Integer userId);

    @Modifying
    @Query(value =
            "INSERT INTO users (first_name, last_name, email, password, " + "proof_of_id, proof_of_address, credit_score, financial_info) " +
                    "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)", nativeQuery = true)
    void createUser(String firstName, String lastName, String email,
                    String password, byte[] proofOfId,
                    byte[] proofOfAddress, byte[] creditScore,
                    byte[] financialInfo);
}
