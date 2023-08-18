package com.socialmedia.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageDto {
    @NotNull(message = "Id не может быть null")
    private Long id;
    @NotNull(message = "Нужно указать id отправителя")
    private Long senderId;
    @NotNull(message = "Нужно указать id получателя")
    private Long receiverId;
    @NotEmpty(message = "Сообщение не может быть пустым")
    private String content;
    private LocalDateTime created;
}
