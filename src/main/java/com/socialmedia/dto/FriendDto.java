package com.socialmedia.dto;

import com.socialmedia.model.Friendship;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FriendDto {
    private Long friendId;
    private List<UserDto> users;
    private Friendship status;
}
