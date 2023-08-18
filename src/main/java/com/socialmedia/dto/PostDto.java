package com.socialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class PostDto {

    private Long id;
    private String header;
    private String description;
    private byte[] image;
    private LocalDateTime created;
    private Long authorId;
}
