package com.socialmedia.controllers;

import com.socialmedia.dto.FriendDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.service.UserService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<UserDto> findAllUsers(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получен запрос GET на вывод всех пользователей");
        Pageable page = PageRequest.of(from, size);
        return userService.findAllUsers(page);
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        log.info("Получен запрос GET на вывод пользователя по id {}", userId);
        return userService.findById(userId);
    }

    @PutMapping("/{friendId}")
    public void addFriend(@RequestHeader("X-SMedia-User-Id") Long userId, @PathVariable Long friendId) {
        log.info("Получен запрос PUT на добавление друга по id {} от пользователя по id {}", friendId, userId);
        userService.addFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<FriendDto> findAllFriends(@PathVariable Long userId) {
        log.info("Получен запрос Get на вывод всех друзей пользователя по id {}", userId);
        return userService.findAllFriends(userId);
    }

    @PutMapping("/friends/{subscriberId}")
    public FriendDto approveFriend(@RequestHeader("X-SMedia-User-Id") Long approvingUserId,
                                   @PathVariable() Long subscriberId, @RequestParam Boolean approved) {
        log.info("Получен запрос PUT на одобрения друга по id {} от пользователя по id {}", approvingUserId,
                subscriberId);
        return userService.approveFriend(approvingUserId, subscriberId, approved);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long userId, @PathVariable() Long friendId) {
        log.info("Получен запрос DELETE на удаления друга по id {} от пользователя по id {}", friendId, userId);
        userService.deleteFriend(userId, friendId);
    }
}
