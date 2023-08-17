package com.socialmedia.service.impl;

import com.socialmedia.dto.FriendshipDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.exceptions.DataNotFoundException;
import com.socialmedia.exceptions.FriendException;
import com.socialmedia.mapper.FriendshipMapper;
import com.socialmedia.mapper.UserMapper;
import com.socialmedia.model.Friendship;
import com.socialmedia.model.Status;
import com.socialmedia.model.Subscription;
import com.socialmedia.model.User;
import com.socialmedia.repository.FriendshipRepository;
import com.socialmedia.repository.SubscriptionRepository;
import com.socialmedia.repository.UserRepository;
import com.socialmedia.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendshipServiceImpl implements FriendshipService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Override
    public FriendshipDto sendFriendRequest(Long requestSenderId, Long receiverId) {
        User sender = checkUserId(requestSenderId);
        User receiver = checkUserId(receiverId);

        if (sender.getFriends().contains(receiver) || receiver.getFriends().contains(sender)) {
            throw new FriendException("Пользователи по id " + requestSenderId + " и " + receiverId +
                    " уже друг у друга в друзьях ");
        }

        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        friendship.setStatus(Status.PENDING);
        friendshipRepository.save(friendship);

        Subscription subscription = new Subscription();
        subscription.setSubscriber(sender);
        subscription.setPublisher(receiver);
        subscriptionRepository.save(subscription);

        return FriendshipMapper.mapToDto(friendship);
    }

    @Override
    public List<UserDto> findAllFriends(Long requestSenderId) {
        User sender = checkUserId(requestSenderId);
        return sender.getFriends()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public FriendshipDto approveFriendship(Long receiverId, Long requestSenderId, Boolean approved) {
        User sender = checkUserId(requestSenderId);
        User receiver = checkUserId(receiverId);

        Friendship friendship = friendshipRepository.findBySenderAndReceiver(sender, receiver);

        if (friendship == null) {
            throw new FriendException("Дружба между пользователем по id " + receiverId + " и " + requestSenderId +
                    " не найдена в базе данных");
        }

        if (approved) {
            friendship.setStatus(Status.APPROVED);
            friendshipRepository.save(friendship);

            sender.getFriends().add(receiver);
            receiver.getFriends().add(sender);
            userRepository.save(sender);
            userRepository.save(receiver);

            Subscription subscription = new Subscription();
            subscription.setSubscriber(receiver);
            subscription.setPublisher(sender);
            subscriptionRepository.save(subscription);
        } else {
            friendship.setStatus(Status.REJECTED);
            friendshipRepository.save(friendship);
        }
        return FriendshipMapper.mapToDto(friendship);
    }

    @Override
    public void deleteFriend(Long requestSenderId, Long receiverId) {
        User sender = checkUserId(requestSenderId);
        User receiver = checkUserId(receiverId);

        Friendship friendship = friendshipRepository.findBySenderAndReceiver(sender, receiver);

        if (friendship == null) {
            subscriptionRepository.deleteBySubscriberAndPublisher(sender, receiver);
        } else {
            friendshipRepository.deleteBySenderAndReceiver(sender, receiver);
            subscriptionRepository.deleteBySubscriberAndPublisher(sender, receiver);
            receiver.getFriends().remove(sender);
            userRepository.save(sender);
            userRepository.save(receiver);
        }
    }

    private User checkUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Пользователь по id " +
                userId + " не найден в базе данных"));
    }
}
