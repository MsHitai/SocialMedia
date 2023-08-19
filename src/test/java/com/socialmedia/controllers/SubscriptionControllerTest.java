package com.socialmedia.controllers;

import com.socialmedia.dto.PostDto;
import com.socialmedia.mapper.PostMapper;
import com.socialmedia.model.Post;
import com.socialmedia.model.Role;
import com.socialmedia.model.Subscription;
import com.socialmedia.model.User;
import com.socialmedia.service.SubscriptionService;
import com.socialmedia.utility.JwtTokenUtils;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SubscriptionService subscriptionService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @Test
    void contextLoad() {
        assertThat(subscriptionService).isNotNull();
    }

    @WithMockUser
    @Test
    void testFindAllPostsByPublisherOkWhenValid() throws Exception {
        User subscriber = User.builder()
                .id(1L)
                .username("testSubscriber")
                .email("test@test.ru")
                .password("password")
                .roles(List.of(new Role()))
                .friends(new HashSet<>())
                .build();

        User publisher = User.builder()
                .id(2L)
                .username("testPublisher")
                .email("test1@test.ru")
                .password("password")
                .roles(List.of(new Role()))
                .friends(new HashSet<>())
                .build();

        Post post1 = new Post();
        post1.setAuthor(publisher);
        post1.setCreated(LocalDateTime.now().minusDays(2));
        Post post2 = new Post();
        post2.setCreated(LocalDateTime.now());
        post2.setAuthor(publisher);

        Subscription subscription = new Subscription();
        subscription.setSubscriber(subscriber);
        subscription.setPublisher(publisher);
        Pageable page = PageRequest.of(0, 20);
        List<Post> posts = new ArrayList<>();
        posts.add(post2);
        posts.add(post1);

        List<PostDto> dtos = posts.stream().map(PostMapper::mapToPostDto).toList();

        when(subscriptionService.findAllPostsByPublisher(subscriber.getId(), page))
                .thenReturn(dtos);

        mvc.perform(get("/subs")
                        .header("X-SMedia-User-Id", subscriber.getId())
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(post2.getId()), Long.class))
                .andExpect(jsonPath("$[0].authorId", is(post2.getAuthor().getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(post1.getId()), Long.class))
                .andExpect(jsonPath("$[0].authorId", is(post1.getAuthor().getId()), Long.class));
    }
}