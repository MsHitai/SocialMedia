package com.socialmedia.controllers;

import com.socialmedia.dto.FriendshipDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.model.Role;
import com.socialmedia.model.Status;
import com.socialmedia.model.User;
import com.socialmedia.service.FriendshipService;
import com.socialmedia.utility.JwtTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
@WebMvcTest(controllers = FriendshipController.class)
class FriendshipControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private FriendshipService friendshipService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    private FriendshipDto dto;
    private User sender;
    private User receiver;
    private Long senderId;
    private Long receiverId;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        receiver = User.builder()
                .id(2L)
                .username("testUsername")
                .email("test@test.ru")
                .password("password")
                .roles(List.of(new Role()))
                .friends(new HashSet<>())
                .build();

        sender = User.builder()
                .id(1L)
                .username("testUsername")
                .email("test@test.ru")
                .password("password")
                .roles(List.of(new Role()))
                .friends(new HashSet<>())
                .build();

        senderId = sender.getId();
        receiverId = receiver.getId();

        dto = new FriendshipDto(
                1L,
                senderId,
                receiverId,
                Status.PENDING
        );
    }

    @Test
    void contextLoad() {
        assertThat(friendshipService).isNotNull();
    }

    @Test
    @WithMockUser
    void testSendFriendRequestOkWhenValid() throws Exception {
        when(friendshipService.sendFriendRequest(senderId, receiverId))
                .thenReturn(dto);

        mvc.perform(put("/friends/" + receiverId)
                        .header("X-SMedia-User-Id", senderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendshipId", is(dto.getFriendshipId()), Long.class))
                .andExpect(jsonPath("$.senderId", is(senderId), Long.class))
                .andExpect(jsonPath("$.receiverId", is(receiverId), Long.class))
                .andExpect(jsonPath("$.status", is(dto.getStatus().toString())));
    }

    @Test
    @WithMockUser
    void testFindAllFriendsOkWhenValid() throws Exception {
        receiver.getFriends().add(sender);
        sender.getFriends().add(receiver);
        UserDto receiverDto = UserDto.builder()
                .id(receiverId)
                .username(receiver.getUsername())
                .email(receiver.getEmail())
                .build();
        when(friendshipService.findAllFriends(senderId))
                .thenReturn(List.of(receiverDto));

        mvc.perform(get("/friends")
                        .header("X-SMedia-User-Id", senderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(receiverDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].username", is(receiverDto.getUsername())))
                .andExpect(jsonPath("$[0].email", is(receiverDto.getEmail())));
    }

    @Test
    @WithMockUser
    void testApproveFriendshipOkWhenValid() throws Exception {
        dto.setStatus(Status.APPROVED);
        when(friendshipService.approveFriendship(receiverId, senderId, true))
                .thenReturn(dto);

        mvc.perform(put("/friends")
                        .header("X-SMedia-User-Id", receiverId)
                        .param("senderId", String.valueOf(senderId))
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.friendshipId", is(dto.getFriendshipId()), Long.class))
                .andExpect(jsonPath("$.senderId", is(senderId), Long.class))
                .andExpect(jsonPath("$.receiverId", is(receiverId), Long.class))
                .andExpect(jsonPath("$.status", is(dto.getStatus().toString())));
    }

    @Test
    @WithMockUser
    void testDeleteFriendOkWhenValid() throws Exception {
        mvc.perform(delete("/friends/" + receiverId)
                        .header("X-SMedia-User-Id", senderId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(friendshipService, times(1))
                .deleteFriend(senderId, receiverId);
    }
}