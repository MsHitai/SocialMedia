package com.socialmedia.mapper;

import com.socialmedia.dto.FriendshipDto;
import com.socialmedia.model.Friendship;
import lombok.experimental.UtilityClass;

@UtilityClass
public class FriendshipMapper {

    public FriendshipDto mapToDto(Friendship friendship) {
        return new FriendshipDto(
                friendship.getId(),
                friendship.getSender().getId(),
                friendship.getReceiver().getId(),
                friendship.getStatus()
        );
    }
}
