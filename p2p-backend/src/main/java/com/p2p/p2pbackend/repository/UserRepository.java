package com.p2p.p2pbackend.repository;

import com.p2p.p2pbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
