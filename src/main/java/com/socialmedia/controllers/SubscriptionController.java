package com.socialmedia.controllers;

import com.socialmedia.dto.PostDto;
import com.socialmedia.service.SubscriptionService;
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
@RequestMapping("/subs")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping()
    public List<PostDto> findAllPostsByPublisher(@RequestHeader("X-SMedia-User-Id") Long userId,
                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                 Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "20")
                                                 Integer size) {
        log.info("Поступил запрос GET на вывод последних постов тех, на кого подписан пользователь по id {}", userId);
        Pageable page = PageRequest.of(from, size);
        return subscriptionService.findAllPostsByPublisher(userId, page);
    }

}
