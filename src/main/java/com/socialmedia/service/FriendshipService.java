package com.socialmedia.service;

import com.socialmedia.dto.FriendshipDto;
import com.socialmedia.dto.UserDto;

import java.util.List;

public interface FriendshipService {

    FriendshipDto sendFriendRequest(Long requestSenderId, Long receiverId);

    List<UserDto> findAllFriends(Long requestSenderId);

    FriendshipDto approveFriendship(Long receiverId, Long requestSenderId, Boolean approved);

    void deleteFriend(Long requestSenderId, Long receiverId);

}
