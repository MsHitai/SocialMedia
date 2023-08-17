package com.socialmedia.service.impl;

import com.socialmedia.dto.FriendDto;
import com.socialmedia.dto.RegistrationDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.exceptions.DataNotFoundException;
import com.socialmedia.mapper.FriendMapper;
import com.socialmedia.mapper.UserMapper;
import com.socialmedia.model.Friend;
import com.socialmedia.model.Friendship;
import com.socialmedia.model.Role;
import com.socialmedia.model.User;
import com.socialmedia.repository.FriendRepository;
import com.socialmedia.repository.RoleRepository;
import com.socialmedia.repository.UserRepository;
import com.socialmedia.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    private final FriendRepository friendRepository;

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

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new DataNotFoundException("Нельзя подружиться самому с собой");
        }
        User submitter = checkUserId(userId);
        checkUserId(friendId);
        Friend friend = Friend.builder()
                .friendId(friendId)
                .status(Friendship.SUBSCRIBER)
                .users(new ArrayList<>())
                .build();
        friend.getUsers().add(submitter);
        friendRepository.save(friend);
    }

    @Override
    public List<FriendDto> findAllFriends(Long userId) {
        User user = checkUserId(userId);
        return user.getFriends().stream()
                .map(FriendMapper::mapToFriendDto)
                .collect(Collectors.toList());
    }

    @Override
    public FriendDto approveFriend(Long approvingUserId, Long subscriberId, Boolean approved) {
        User approvingUser = checkUserId(approvingUserId);
        Friend subscriber = checkFriendId(subscriberId);
        if (approved) {
            subscriber.setStatus(Friendship.FRIEND);
            Friend friend = Friend.builder()
                    .friendId(subscriberId)
                    .status(Friendship.FRIEND)
                    .build();
            subscriber.getUsers().add(approvingUser);
            approvingUser.getFriends().add(subscriber);
            userRepository.save(approvingUser);
            friendRepository.save(friend);
            friendRepository.save(subscriber);
        }
        return FriendMapper.mapToFriendDto(subscriber);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        User submitter = checkUserId(userId);
        Friend friend = checkFriendId(friendId);
        friendRepository.deleteByFriendId(friendId);
        submitter.getFriends().remove(friend);

    }

    private User checkUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Пользователь по id " +
                userId + " не найден в базе данных"));
    }

    private Friend checkFriendId(Long friendId) {
        return friendRepository.findById(friendId).orElseThrow(() -> new DataNotFoundException("Друг по id " +
                friendId + " не найден в базе данных"));
    }
}
