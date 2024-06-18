package com.p2p.p2pbackend.service.impl;

import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.entity.User;
import com.p2p.p2pbackend.exception.ResourceNotFoundException;
import com.p2p.p2pbackend.mapper.UserMapper;
import com.p2p.p2pbackend.repository.UserRepository;
import com.p2p.p2pbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        // Map DTO to entity
        User user = UserMapper.mapToUser(userDto);

        // Encode the password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user entity
        User savedUser = userRepository.save(user);

        // Map entity to DTO and return
        return UserMapper.mapToUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(int userId) {
        // Find user by ID or throw an exception if not found
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist: " + userId));

        // Map entity to DTO and return
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        // Find user by email or throw an exception if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist with email: " + email));

        // Map entity to DTO and return
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto signIn(String email, String password) {
        // Find user by email or throw an exception if not found
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User does not exist with email: " + email));

        // Check if the provided password matches the stored hashed password
        if (passwordEncoder.matches(password, user.getPassword())) {
            // Map entity to DTO and return
            return UserMapper.mapToUserDto(user);
        } else {
            // Throw an exception if the password is invalid
            throw new IllegalArgumentException("Invalid email or password");
        }
    }
}
