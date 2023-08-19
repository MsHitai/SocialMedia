package com.socialmedia.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialmedia.dto.MessageDto;
import com.socialmedia.model.Role;
import com.socialmedia.model.User;
import com.socialmedia.service.MessageService;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MessageController.class)
class MessageControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private MessageService messageService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private ObjectMapper mapper;

    private MessageDto dto;
    private User sender;
    private User receiver;

    private final LocalDateTime created =
            LocalDateTime.of(2023, Month.AUGUST, 19, 9, 10, 1);

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

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        dto = new MessageDto(
                1L,
                sender.getId(),
                receiver.getId(),
                "some test content",
                created
        );
    }

    @Test
    void contextLoad() {
        assertThat(messageService).isNotNull();
    }

    @Test
    @WithMockUser
    void testFindAllMessagesOkWhenValid() throws Exception {
        when(messageService.findAllMessages(sender.getId(), receiver.getId()))
                .thenReturn(List.of(dto));

        mvc.perform(get("/messages")
                        .header("X-SMedia-User-Id", sender.getId())
                        .param("friendId", String.valueOf(receiver.getId()))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].senderId", is(sender.getId()), Long.class))
                .andExpect(jsonPath("$[0].receiverId", is(receiver.getId()), Long.class))
                .andExpect(jsonPath("$[0].content", is(dto.getContent())))
                .andExpect(jsonPath("$[0].created", is(dto.getCreated().toString())));
    }

    @Test
    @WithMockUser
    void testAddMessageOkWhenValid() throws Exception {
        when(messageService.addMessage(sender.getId(), receiver.getId(), dto))
                .thenReturn(dto);

        mvc.perform(post("/messages/" + receiver.getId())
                        .header("X-SMedia-User-Id", sender.getId())
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.senderId", is(sender.getId()), Long.class))
                .andExpect(jsonPath("$.receiverId", is(receiver.getId()), Long.class))
                .andExpect(jsonPath("$.content", is(dto.getContent())))
                .andExpect(jsonPath("$.created", is(dto.getCreated().toString())));
    }
}