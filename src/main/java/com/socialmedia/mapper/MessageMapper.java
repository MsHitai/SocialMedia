package com.socialmedia.mapper;

import com.socialmedia.dto.MessageDto;
import com.socialmedia.model.Message;
import com.socialmedia.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageMapper {

    public MessageDto mapToDto(Message message) {
        return new MessageDto(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getCreated()
        );
    }

    public Message mapToMessage(MessageDto messageDto, User sender, User receiver) {
        return new Message(
                messageDto.getId(),
                sender,
                receiver,
                messageDto.getContent(),
                messageDto.getCreated()
        );
    }
}
