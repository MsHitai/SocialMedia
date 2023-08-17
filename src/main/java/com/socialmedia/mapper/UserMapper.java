package com.socialmedia.mapper;

import com.socialmedia.dto.RegistrationDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.model.Role;
import com.socialmedia.model.User;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class UserMapper {

    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

    public User mapToUser(RegistrationDto registrationUserDto, String password, List<Role> roles) {
        return User.builder()
                .username(registrationUserDto.getUsername())
                .email(registrationUserDto.getEmail())
                .password(password)
                .roles(roles)
                .build();
    }
}
