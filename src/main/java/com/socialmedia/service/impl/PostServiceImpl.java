package com.socialmedia.service.impl;

import com.socialmedia.dto.PostDto;
import com.socialmedia.exceptions.DataNotFoundException;
import com.socialmedia.mapper.PostMapper;
import com.socialmedia.model.Post;
import com.socialmedia.model.User;
import com.socialmedia.repository.PostRepository;
import com.socialmedia.repository.UserRepository;
import com.socialmedia.service.PostService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PostDto> findAllPosts(Pageable page) {
        return postRepository.findAll(page).stream()
                .map(PostMapper::mapToPostDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostDto addPost(Long userId, MultipartFile file, String description, String header) throws IOException {
        Post post = new Post();
        User user = checkUserId(userId);
        post.setAuthor(user);
        post.setHeader(header);
        post.setDescription(description);
        if (file != null) {
            post.setImage(file.getBytes());
        }
        post.setCreated(LocalDateTime.now());
        return PostMapper.mapToPostDto(postRepository.save(post));
    }

    @Override
    public PostDto updatePost(Long userId, Long postId, MultipartFile file, String description) throws IOException {
        Post post = checkPostId(postId);
        User user = checkUserId(userId);
        checkIfUserOwner(post, user);
        post.setHeader(file.getName());
        post.setDescription(description);
        post.setImage(file.getBytes());
        post.setCreated(LocalDateTime.now());
        return PostMapper.mapToPostDto(postRepository.save(post));
    }

    @Override
    public PostDto findPostById(Long postId, Long userId) {
        Post post = checkPostId(postId);
        checkUserId(userId);
        return PostMapper.mapToPostDto(post);
    }

    @Override
    public void deletePost(Long userId, Long postId) {
        Post post = checkPostId(postId);
        User user = checkUserId(userId);
        checkIfUserOwner(post, user);
        postRepository.deleteById(postId);
    }

    private void checkIfUserOwner(Post post, User user) {
        if (!Objects.equals(post.getAuthor().getId(), user.getId())) {
            throw new DataNotFoundException("Пользователь по id " +
                    user.getId() + " не делал пост по id " + post.getId());
        }
    }

    private Post checkPostId(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new DataNotFoundException("Пост по id " +
                postId + " не найден в базе данных"));
    }

    private User checkUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Пользователь по id " +
                userId + " не найден в базе данных"));
    }
}
