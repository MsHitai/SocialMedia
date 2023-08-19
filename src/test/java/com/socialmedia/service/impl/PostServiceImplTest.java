package com.socialmedia.service.impl;

import com.socialmedia.dto.PostDto;
import com.socialmedia.exceptions.DataNotFoundException;
import com.socialmedia.model.Post;
import com.socialmedia.model.Role;
import com.socialmedia.model.User;
import com.socialmedia.repository.PostRepository;
import com.socialmedia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl service;

    private Post post;
    private PostDto dto;
    private User user;
    private Long userId;
    private final LocalDateTime created =
            LocalDateTime.of(2023, Month.AUGUST, 19, 9, 10, 1);

    @BeforeEach
    void setUp() {
        userId = 1L;

        user = User.builder()
                .id(userId)
                .username("testUsername")
                .email("test@test.ru")
                .password("password")
                .roles(List.of(new Role()))
                .friends(new HashSet<>())
                .build();

        dto = PostDto.builder()
                .id(1L)
                .description("testDescription")
                .header("testHeader")
                .created(created)
                .image(new byte[]{})
                .authorId(userId)
                .build();

        post = Post.builder()
                .id(1L)
                .description("testDescription")
                .header("testHeader")
                .created(created)
                .image(new byte[]{})
                .author(user)
                .build();
    }

    @Test
    void testFindAllPostsOkWhenValid() {
        List<Post> posts = Collections.singletonList(post);
        Page<Post> postPage = new PageImpl<>(posts);
        Pageable page = PageRequest.of(0, 20);

        when(postRepository.findAll(page))
                .thenReturn(postPage);

        List<PostDto> result = service.findAllPosts(page);
        PostDto actualPost = result.get(0);

        assertThat(actualPost.getId(), is(dto.getId()));
        assertThat(actualPost.getHeader(), is(dto.getHeader()));
        assertThat(actualPost.getDescription(), is(dto.getDescription()));
        assertThat(actualPost.getCreated(), is(dto.getCreated()));
    }

    @Test
    void testAddPostOkWhenValid() throws IOException {
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));
        when(postRepository.save(any(Post.class)))
                .thenReturn(post);

        PostDto actualPost = service.addPost(userId, null, "testDescription", "testHeader");

        assertThat(actualPost.getId(), is(dto.getId()));
        assertThat(actualPost.getHeader(), is(dto.getHeader()));
        assertThat(actualPost.getDescription(), is(dto.getDescription()));
        assertThat(actualPost.getCreated(), is(dto.getCreated()));
    }

    @Test
    void testUpdatePostOkWhenValid() throws IOException {
        post.setDescription("newDescription");
        post.setHeader("newHeader");
        dto.setHeader(post.getHeader());
        dto.setDescription(post.getDescription());
        when(postRepository.findById(post.getId()))
                .thenReturn(Optional.ofNullable(post));
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));
        when(postRepository.save(any(Post.class)))
                .thenReturn(post);

        PostDto actualPost = service.updatePost(userId, post.getId(), null, "newDescription",
                "newHeader");

        assertThat(actualPost.getId(), is(dto.getId()));
        assertThat(actualPost.getHeader(), is(dto.getHeader()));
        assertThat(actualPost.getDescription(), is(dto.getDescription()));
    }

    @Test
    void testUpdatePost404WhenWrongPostId() {
        long wrongId = 22L;
        post.setDescription("newDescription");
        post.setHeader("newHeader");
        dto.setHeader(post.getHeader());
        dto.setDescription(post.getDescription());
        when(postRepository.findById(wrongId))
                .thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () ->
                service.updatePost(userId, wrongId, null, "newDescription", "newHeader"));
    }

    @Test
    void testUpdatePost404WhenWrongUserId() {
        long wrongId = 22L;
        post.setDescription("newDescription");
        post.setHeader("newHeader");
        dto.setHeader(post.getHeader());
        dto.setDescription(post.getDescription());
        when(postRepository.findById(post.getId()))
                .thenReturn(Optional.ofNullable(post));
        when(userRepository.findById(wrongId))
                .thenReturn(Optional.empty());

        assertThrows(DataNotFoundException.class, () ->
                service.updatePost(wrongId, post.getId(), null, "newDescription", "newHeader"));
    }

    @Test
    void testUpdatePost404WhenUserIsNotOwner() {
        Post post1 = new Post();
        post1.setId(22L);
        User anotherUser = new User();
        anotherUser.setId(22L);
        post1.setAuthor(anotherUser);
        post.setDescription("newDescription");
        post.setHeader("newHeader");
        dto.setHeader(post.getHeader());
        dto.setDescription(post.getDescription());
        when(postRepository.findById(post1.getId()))
                .thenReturn(Optional.of(post1));
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        assertThrows(DataNotFoundException.class, () ->
                service.updatePost(userId, post1.getId(), null, "newDescription", "newHeader"));
    }

    @Test
    void testFindPostByIdOkWhenValid() {
        when(postRepository.findById(post.getId()))
                .thenReturn(Optional.ofNullable(post));
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        PostDto actualPost = service.findPostById(post.getId(), userId);

        assertThat(actualPost.getId(), is(dto.getId()));
        assertThat(actualPost.getHeader(), is(dto.getHeader()));
        assertThat(actualPost.getDescription(), is(dto.getDescription()));
    }

    @Test
    void testDeletePostOkWhenValid() {
        when(postRepository.findById(post.getId()))
                .thenReturn(Optional.ofNullable(post));
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));

        service.deletePost(userId, post.getId());

        verify(postRepository, times(1))
                .deleteById(post.getId());
    }
}