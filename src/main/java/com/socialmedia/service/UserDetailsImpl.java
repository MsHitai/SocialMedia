package com.socialmedia.service;

import com.socialmedia.dto.RegistrationDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.exceptions.DataNotFoundException;
import com.socialmedia.mapper.UserMapper;
import com.socialmedia.model.Role;
import com.socialmedia.model.User;
import com.socialmedia.repository.RoleRepository;
import com.socialmedia.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetailsService, UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new
                DataNotFoundException(String.format("Пользователь '%s' не найден", username)));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User createNewUser(RegistrationDto registrationUserDto) {
        String password = passwordEncoder.encode(registrationUserDto.getPassword());
        List<Role> roles = List.of(roleRepository.findByName("ROLE_USER"));
        User user = UserMapper.mapToUser(registrationUserDto, password, roles);
        return userRepository.save(user);
    }

    @Override
    public List<UserDto> findAllUsers(Pageable page) {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new
                DataNotFoundException(String.format("Пользователь по id '%d' не найден", userId)));
        return UserMapper.mapToUserDto(user);
    }


}
