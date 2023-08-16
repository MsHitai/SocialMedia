package com.socialmedia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotNull(message = "Имя пользователя не может быть пустым")
    private String username;

    @Email(message = "Email введен некорректно")
    @NotNull(message = "Email не может быть пустым")
    private String email;

}
