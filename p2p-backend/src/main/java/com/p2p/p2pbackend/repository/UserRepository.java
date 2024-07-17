package com.p2p.p2pbackend.repository;

import com.p2p.p2pbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    List<User> findByActiveStatusFalse();
    Optional<User> findByUserId(int userId);
    List<User> findByActiveStatusTrue();
    Optional<User> deleteById(int userId);

}
