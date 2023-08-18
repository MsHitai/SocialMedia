package com.socialmedia.service.impl;

import com.socialmedia.dto.MessageDto;
import com.socialmedia.exceptions.DataNotFoundException;
import com.socialmedia.mapper.MessageMapper;
import com.socialmedia.model.Message;
import com.socialmedia.model.User;
import com.socialmedia.repository.MessageRepository;
import com.socialmedia.repository.UserRepository;
import com.socialmedia.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;


    @Override
    public List<MessageDto> findAllMessages(Long userId, Long friendId) {
        User sender = checkUserId(userId);
        User receiver = checkUserId(userId);
        return messageRepository.findAllBySenderAndReceiver(sender, receiver).stream()
                .map(MessageMapper::mapToDto).collect(Collectors.toList());
    }

    @Override
    public MessageDto addMessage(Long senderId, Long friendId, MessageDto messageDto) {
        User sender = checkUserId(senderId);
        User receiver = checkUserId(friendId);
        messageDto.setCreated(LocalDateTime.now());
        Message message = MessageMapper.mapToMessage(messageDto, sender, receiver);
        return MessageMapper.mapToDto(messageRepository.save(message));
    }

    private User checkUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Пользователь по id " +
                userId + " не найден в базе данных"));
    }
}
