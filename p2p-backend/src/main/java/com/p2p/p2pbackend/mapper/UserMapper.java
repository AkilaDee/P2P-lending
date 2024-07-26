package com.p2p.p2pbackend.mapper;

import com.p2p.p2pbackend.dto.UserDto;
import com.p2p.p2pbackend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getProofOfId(),
                user.getProofOfAddress(),
                user.getFinancialInfo(),
                user.getCreditScore(),
                user.getActiveStatus(),
                user.getRating()
        );
    }

    public static User mapToUser(UserDto userDto){
        return new User(
                userDto.getUserId(),
                userDto.getFirstName(),
                userDto.getLastName(),
                userDto.getEmail(),
                userDto.getPassword(),
                userDto.getProofOfId(),
                userDto.getProofOfAddress(),
                userDto.getFinancialInfo(),
                userDto.getCreditScore(),
                userDto.getActiveStatus(),
                userDto.getRating()
        );

    }
}