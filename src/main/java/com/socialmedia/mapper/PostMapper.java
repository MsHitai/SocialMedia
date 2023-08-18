package com.socialmedia.mapper;

import com.socialmedia.dto.PostDto;
import com.socialmedia.model.Post;
import com.socialmedia.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PostMapper {

    public Post mapToPost(PostDto postDto, User user) {
        return Post.builder()
                .id(postDto.getId())
                .image(postDto.getImage())
                .header(postDto.getHeader())
                .description(postDto.getDescription())
                .author(user)
                .created(postDto.getCreated())
                .build();
    }

    public PostDto mapToPostDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .header(post.getHeader())
                .description(post.getDescription())
                .image(post.getImage())
                .created(post.getCreated())
                .authorId(post.getAuthor().getId())
                .build();
    }
}
