package com.p2p.p2pbackend.service;

import com.p2p.p2pbackend.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface AdminService {
    List<UserDto> getAllInactiveUsers();
    UserDto acceptNewUser(Map<String, Integer> requestMap);
    boolean deleteUser(Map<String, String> requestMap);
    List<UserDto> getAllActiveUsers();
    UserDto disableUsers(Map<String, Integer> requestMap);

}
