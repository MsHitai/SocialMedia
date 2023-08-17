package com.socialmedia.controllers;

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

    @PutMapping("/{userId}")
    public UserDto updateUser(@RequestHeader("X-SMedia-User-Id") Long id,
                              @PathVariable Long userId) {
        return null; // todo implement
    }
}
