package com.socialmedia.service;

import com.socialmedia.dto.RegistrationDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.model.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);

    User createNewUser(RegistrationDto registrationUserDto);

    List<UserDto> findAllUsers(Pageable page);

    UserDto findById(long userId);

    UserDto updateUser(Long userId, String currentUsername, RegistrationDto dto);

}
