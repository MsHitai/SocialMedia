package com.socialmedia.controllers;

import com.socialmedia.dto.MessageDto;
import com.socialmedia.service.MessageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping()
    public List<MessageDto> findAllMessages(@RequestHeader("X-SMedia-User-Id") Long userId,
                                            @RequestParam(name = "friendId") Long friendId) {
        log.info("Поступил запрос GET на вывод всех сообщений пользователя по id {} c пользователем по id {}", userId,
                friendId);
        return messageService.findAllMessages(userId, friendId);
    }

    @PostMapping("/{friendId}")
    public MessageDto addMessage(@RequestHeader("X-SMedia-User-Id") Long senderId,
                                 @PathVariable Long friendId,
                                 @Valid @RequestBody MessageDto messageDto) {
        log.info("Поступил запрос POST на добавление сообщения от пользователя по id {} пользователю по id {}",
                senderId, friendId);
        return messageService.addMessage(senderId, friendId, messageDto);
    }
}
