package com.socialmedia.mapper;

import com.socialmedia.dto.FriendDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.model.Friend;
import com.socialmedia.model.User;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class FriendMapper {

    public FriendDto mapToFriendDto(Friend friend) {
        List<UserDto> users = friend.getUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
        return new FriendDto(
                friend.getFriendId(),
                users,
                friend.getStatus()
        );
    }

    public Friend mapToFriend(FriendDto dto, List<User> users) {
        return Friend.builder()
                .friendId(dto.getFriendId())
                .users(users)
                .status(dto.getStatus())
                .build();
    }
}
