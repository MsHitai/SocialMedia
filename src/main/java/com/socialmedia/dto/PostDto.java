package com.socialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PostDto {

    private Long id;
    private String header;
    private String description;
    private byte[] image;
    private Long authorId;
}
