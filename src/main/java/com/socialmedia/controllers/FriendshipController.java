package com.socialmedia.controllers;

import com.socialmedia.dto.FriendshipDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.service.FriendshipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@Slf4j
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }

    @PutMapping("/{receiverId}")
    public FriendshipDto sendFriendRequest(@RequestHeader("X-SMedia-User-Id") Long requestSenderId,
                                           @PathVariable Long receiverId) {
        log.info("Получен запрос PUT на добавление друга по id {} от пользователя по id {}", receiverId,
                requestSenderId);
        return friendshipService.sendFriendRequest(requestSenderId, receiverId);
    }

    @GetMapping()
    public List<UserDto> findAllFriends(@RequestHeader("X-SMedia-User-Id") Long requestSenderId) {
        log.info("Получен запрос Get на вывод всех друзей пользователя по id {}", requestSenderId);
        return friendshipService.findAllFriends(requestSenderId);
    }

    @PutMapping()
    public FriendshipDto approveFriendship(@RequestHeader("X-SMedia-User-Id") Long receiverId,
                                           @RequestParam(name = "senderId") Long requestSenderId,
                                           @RequestParam(name = "approved") Boolean approved) {
        log.info("Получен запрос PUT на одобрения друга по id {}", requestSenderId);
        return friendshipService.approveFriendship(receiverId, requestSenderId, approved);
    }

    @DeleteMapping("/{receiverId}")
    public void deleteFriend(@RequestHeader("X-SMedia-User-Id") Long requestSenderId,
                             @PathVariable Long receiverId) {
        log.info("Получен запрос DELETE на удаления друга по id {} от пользователя по id {}", receiverId,
                requestSenderId);
        friendshipService.deleteFriend(requestSenderId, receiverId);
    }
}
