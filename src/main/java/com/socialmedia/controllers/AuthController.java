package com.socialmedia.controllers;

import com.socialmedia.dto.JwtRequest;
import com.socialmedia.dto.RegistrationDto;
import com.socialmedia.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@Valid @RequestBody JwtRequest authRequest) {
        log.info("Поступил запрос на получения токена от {}", authRequest.getUsername());
        return authService.createAuthToken(authRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@Valid @RequestBody RegistrationDto registrationUserDto) {
        log.info("Поступил запрос на регистрацию пользователя {}", registrationUserDto.getUsername());
        return authService.createNewUser(registrationUserDto);
    }
}
