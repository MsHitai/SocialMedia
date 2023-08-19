package com.socialmedia.controllers;

import com.socialmedia.dto.PostDto;
import com.socialmedia.model.Role;
import com.socialmedia.model.User;
import com.socialmedia.service.PostService;
import com.socialmedia.utility.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = PostController.class)
class PostControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    private PostDto dto;

    private User publisher;
    private Long postId;

    private final LocalDateTime created =
            LocalDateTime.of(2023, Month.AUGUST, 19, 9, 10, 1);

    private Pageable page;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        postId = 1L;
        page = PageRequest.of(0, 20);

        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        publisher = User.builder()
                .id(1L)
                .username("test")
                .email("test1@test.ru")
                .password("password")
                .roles(List.of(role1))
                .friends(new HashSet<>())
                .build();

        dto = PostDto.builder()
                .id(postId)
                .authorId(publisher.getId())
                .created(created)
                .header("header")
                .description("description")
                .build();
    }

    @Test
    void contextLoad() {
        assertThat(postService).isNotNull();
    }

    @Test
    @WithMockUser
    void testFindAllPostsOkWhenValid() throws Exception {
        when(postService.findAllPosts(page))
                .thenReturn(List.of(dto));

        mvc.perform(get("/posts")
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].authorId", is(publisher.getId()), Long.class))
                .andExpect(jsonPath("$[0].header", is(dto.getHeader())))
                .andExpect(jsonPath("$[0].description", is(dto.getDescription())))
                .andExpect(jsonPath("$[0].created", is(dto.getCreated().toString())));
    }

    @Test
    @WithMockUser
    void testFindPostByIdOkWhenValid() throws Exception {
        when(postService.findPostById(postId, publisher.getId()))
                .thenReturn(dto);

        mvc.perform(get("/posts/" + postId)
                        .header("X-SMedia-User-Id", publisher.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(publisher.getId()), Long.class))
                .andExpect(jsonPath("$.header", is(dto.getHeader())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.created", is(dto.getCreated().toString())));
    }

    @WithMockUser
    @Test
    void testAddPostOkWhenValid() throws Exception {
        when(postService.addPost(publisher.getId(), null, "description", "header"))
                .thenReturn(dto);

        mvc.perform(post("/posts")
                        .header("X-SMedia-User-Id", publisher.getId())
                        .param("header", "header")
                        .param("description", "description")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(publisher.getId()), Long.class))
                .andExpect(jsonPath("$.header", is(dto.getHeader())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.created", is(dto.getCreated().toString())));
    }

    @Test
    @WithMockUser
    void updatePost() throws Exception {
        dto.setDescription("newDescription");
        dto.setHeader("newHeader");
        when(postService.updatePost(publisher.getId(), postId, null, "newDescription", "newHeader"))
                .thenReturn(dto);

        mvc.perform(put("/posts/" + postId)
                        .header("X-SMedia-User-Id", publisher.getId())
                        .param("header", "newHeader")
                        .param("description", "newDescription")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(publisher.getId()), Long.class))
                .andExpect(jsonPath("$.header", is(dto.getHeader())))
                .andExpect(jsonPath("$.description", is(dto.getDescription())))
                .andExpect(jsonPath("$.created", is(dto.getCreated().toString())));
    }

    @Test
    @WithMockUser
    void deletePost() throws Exception {
        mvc.perform(delete("/posts/" + postId)
                        .header("X-SMedia-User-Id", publisher.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(postService, times(1))
                .deletePost(publisher.getId(), postId);
    }
}