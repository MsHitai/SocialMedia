package com.socialmedia.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialmedia.dto.RegistrationDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.model.Role;
import com.socialmedia.model.User;
import com.socialmedia.service.UserService;
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

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    private MockMvc mvc;

    @MockBean
    private UserService service;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    private UserDto dto;
    private User user;

    private long userId;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        userId = 1L;

        dto = UserDto.builder()
                .id(userId)
                .username("TestBob")
                .email("test@test.ru")
                .build();

        user = User.builder()
                .id(userId)
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password("password")
                .roles(List.of(new Role()))
                .friends(new HashSet<>())
                .build();
    }

    @Test
    void contextLoad() {
        assertThat(service).isNotNull();
    }

    @WithMockUser()
    @Test
    void testFindAllUsersOkWhenValid() throws Exception {
        Pageable page = PageRequest.of(0, 20);
        when(service.findAllUsers(page))
                .thenReturn(List.of(dto));

        mvc.perform(get("/users")
                        .param("from", "0")
                        .param("size", "20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$[0].username", is(dto.getUsername())))
                .andExpect(jsonPath("$[0].email", is(dto.getEmail())));
    }

    @WithMockUser()
    @Test
    void testFindByIdOkWhenValid() throws Exception {
        when(service.findById(userId))
                .thenReturn(dto);

        mvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId()), Long.class))
                .andExpect(jsonPath("$.username", is(dto.getUsername())))
                .andExpect(jsonPath("$.email", is(dto.getEmail())));
    }

    @WithMockUser()
    @Test
    void testUpdateUserOkWhenValid() throws Exception {
        UserDto newDto = UserDto.builder()
                .id(userId)
                .username("newTestUsername")
                .email("test@test.ru")
                .build();
        RegistrationDto registrationDto = RegistrationDto.builder()
                .id(userId)
                .username("newTestUsername")
                .email(dto.getEmail())
                .password("password")
                .confirmPassword("password")
                .build();

        when(service.updateUser(anyLong(), anyString(), any(RegistrationDto.class)))
                .thenReturn(newDto);

        mvc.perform(put("/users/" + userId)
                        .content(mapper.writeValueAsString(registrationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(newDto.getId()), Long.class))
                .andExpect(jsonPath("$.username", is(newDto.getUsername())))
                .andExpect(jsonPath("$.email", is(newDto.getEmail())));
    }
}