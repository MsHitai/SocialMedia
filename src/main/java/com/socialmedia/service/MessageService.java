package com.socialmedia.service;

import com.socialmedia.dto.MessageDto;

import java.util.List;

public interface MessageService {
    List<MessageDto> findAllMessages(Long userId, Long friendId);

    MessageDto addMessage(Long senderId, Long friendId, MessageDto messageDto);

}
