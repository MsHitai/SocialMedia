package com.socialmedia.controllers;

import com.socialmedia.dto.PostDto;
import com.socialmedia.service.PostService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts")
@Slf4j
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping()
    public List<PostDto> findAllPosts(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                      @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        log.info("Получен запрос GET на вывод всех постов");
        Pageable page = PageRequest.of(from, size);
        return postService.findAllPosts(page);
    }

    @GetMapping("/{postId}")
    public PostDto findPostById(@RequestHeader("X-SMedia-User-Id") Long userId,
                                @PathVariable() Long postId) {
        log.info("Получен запрос GET на вывод поста по id {} от пользователя по id {}", postId, userId);
        return postService.findPostById(postId, userId);
    }

    @PostMapping()
    public PostDto addPost(@RequestHeader("X-SMedia-User-Id") Long userId,
                           @RequestPart(required = false, name = "image") MultipartFile file,
                           @RequestParam("header") String header,
                           @RequestParam("description") String description) throws IOException {
        log.info("Получен запрос POST на добавление поста от пользователя по id {}", userId);
        return postService.addPost(userId, file, description, header);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@RequestHeader("X-SMedia-User-Id") Long userId,
                              @PathVariable() Long postId,
                              @RequestPart(required = false, name = "image") MultipartFile file,
                              @RequestParam("header") String header,
                              @RequestParam("description") String description) throws IOException {
        log.info("Получен запрос PUT на обновление поста по id {} от пользователя по id {}", postId, userId);
        return postService.updatePost(userId, postId, file, description, header);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@RequestHeader("X-SMedia-User-Id") Long userId,
                           @PathVariable() Long postId) {
        log.info("Получен запрос DELETE на удаление поста по id {} от пользователя по id {}", postId, userId);
        postService.deletePost(userId, postId);
    }
}
