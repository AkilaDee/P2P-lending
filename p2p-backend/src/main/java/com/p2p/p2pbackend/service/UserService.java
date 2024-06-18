package com.p2p.p2pbackend.service;

import com.p2p.p2pbackend.dto.UserDto;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUserById(int userId);
    UserDto getUserByEmail(String email);
    UserDto signIn(String email, String password);
}
