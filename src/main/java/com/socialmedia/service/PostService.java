package com.socialmedia.service;

import com.socialmedia.dto.PostDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    List<PostDto> findAllPosts(Pageable page);

    PostDto addPost(Long userId, MultipartFile file, String description, String header) throws IOException;

    PostDto updatePost(Long userId, Long postId, MultipartFile file, String description, String header) throws IOException;

    PostDto findPostById(Long postId, Long userId);

    void deletePost(Long userId, Long postId);

}
