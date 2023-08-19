package com.socialmedia.service.impl;

import com.socialmedia.dto.RegistrationDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.exceptions.AuthenticationException;
import com.socialmedia.exceptions.DataNotFoundException;
import com.socialmedia.model.Role;
import com.socialmedia.model.User;
import com.socialmedia.repository.RoleRepository;
import com.socialmedia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserDetailsImpl service;

    private UserDto dto;
    private RegistrationDto registrationDto;
    private Role role;
    private User user;
    private Long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        user = User.builder()
                .id(userId)
                .username("testUsername")
                .email("test@test.ru")
                .password("password")
                .roles(List.of(new Role()))
                .friends(new HashSet<>())
                .build();

        dto = UserDto.builder()
                .id(userId)
                .username(user.getUsername())
                .email(user.getEmail())
                .build();

        registrationDto = RegistrationDto.builder()
                .id(userId)
                .username("testUsername")
                .email(dto.getEmail())
                .password("password")
                .confirmPassword("password")
                .build();

        role = new Role(
                1L,
                "ROLE_USER",
                List.of(user)
        );
    }

    @Test
    void testFindByUsernameOkWhenCorrectUsername() {
        when(userRepository.findByUsername("testUsername"))
                .thenReturn(Optional.ofNullable(user));

        User actualUser = service.findByUsername("testUsername").get();

        assertThat(actualUser.getId(), is(userId));
        assertThat(actualUser.getUsername(), is(user.getUsername()));
        assertThat(actualUser.getEmail(), is(user.getEmail()));
    }

    @Test
    void testCreateNewUserOkWhenValid() {
        when(passwordEncoder.encode(registrationDto.getPassword()))
                .thenReturn(anyString());
        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(role);
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User actualUser = service.createNewUser(registrationDto);

        assertThat(actualUser.getId(), is(userId));
        assertThat(actualUser.getUsername(), is(user.getUsername()));
        assertThat(actualUser.getEmail(), is(user.getEmail()));
    }

    @Test
    void testFindAllUsersOkWhenValid() {
        List<User> userList = Collections.singletonList(user);
        Page<User> userPage = new PageImpl<>(userList);
        Pageable page = PageRequest.of(0, 20);

        when(userRepository.findAll(page))
                .thenReturn(userPage);

        List<UserDto> result = service.findAllUsers(page);
        UserDto actualUser = result.get(0);

        assertThat(actualUser.getId(), is(userId));
        assertThat(actualUser.getUsername(), is(dto.getUsername()));
        assertThat(actualUser.getEmail(), is(dto.getEmail()));
    }

    @Test
    void testFindByIdOkWhenCorrectId() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        UserDto actualUser = service.findById(userId);

        assertThat(actualUser.getId(), is(userId));
        assertThat(actualUser.getUsername(), is(dto.getUsername()));
        assertThat(actualUser.getEmail(), is(dto.getEmail()));
    }

    @Test
    void testFindById404WhenWrongId() {
        long wrongId = 22L;
        when(userRepository.findById(wrongId))
                .thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () -> service.findById(wrongId));
    }

    @Test
    void testUpdateUserOkWhenValid() {
        registrationDto.setUsername("newUsername");
        dto.setUsername(registrationDto.getUsername());
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto actualUser = service.updateUser(userId, "testUsername", registrationDto);

        assertThat(actualUser.getId(), is(userId));
        assertThat(actualUser.getUsername(), is(dto.getUsername()));
        assertThat(actualUser.getEmail(), is(dto.getEmail()));
    }

    @Test
    void testUpdateUserFailWhenWrongUsername() {
        registrationDto.setUsername("newUsername");
        dto.setUsername(registrationDto.getUsername());
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(AuthenticationException.class,
                () -> service.updateUser(userId, "wrongUsername", registrationDto));

        verify(userRepository, never())
                .save(any(User.class));
    }
}