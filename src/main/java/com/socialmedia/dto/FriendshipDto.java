package com.socialmedia.dto;

import com.socialmedia.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FriendshipDto {
    private Long friendshipId;
    private Long senderId;
    private Long receiverId;
    private Status status;
}
