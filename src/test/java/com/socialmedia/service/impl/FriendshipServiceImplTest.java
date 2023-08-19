package com.socialmedia.service.impl;

import com.socialmedia.dto.FriendshipDto;
import com.socialmedia.dto.UserDto;
import com.socialmedia.exceptions.FriendException;
import com.socialmedia.model.*;
import com.socialmedia.repository.FriendshipRepository;
import com.socialmedia.repository.SubscriptionRepository;
import com.socialmedia.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @InjectMocks
    private FriendshipServiceImpl service;

    private FriendshipDto dto;
    private Friendship friendship;
    private Subscription subscription;
    private User sender;
    private User receiver;
    private Long senderId;
    private Long receiverId;

    @BeforeEach
    void setUp() {
        receiver = User.builder()
                .id(2L)
                .username("testUsername")
                .email("test@test.ru")
                .password("password")
                .roles(List.of(new Role()))
                .friends(new HashSet<>())
                .build();

        sender = User.builder()
                .id(1L)
                .username("testUsername")
                .email("test2@test.ru")
                .password("password")
                .roles(List.of(new Role()))
                .friends(new HashSet<>())
                .build();

        senderId = sender.getId();
        receiverId = receiver.getId();

        dto = new FriendshipDto(
                1L,
                senderId,
                receiverId,
                Status.PENDING
        );

        friendship = new Friendship(
                dto.getFriendshipId(),
                sender,
                receiver,
                Status.PENDING
        );

        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setPublisher(receiver);
        subscription.setSubscriber(sender);
    }

    @Test
    void testSendFriendRequestOkWhenValid() {
        when(userRepository.findById(senderId))
                .thenReturn(Optional.ofNullable(sender));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.ofNullable(receiver));
        when(friendshipRepository.save(any(Friendship.class)))
                .thenAnswer(invocation -> {
                    Friendship savedFriendship = invocation.getArgument(0);
                    savedFriendship.setId(1L);
                    return savedFriendship;
                });
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        FriendshipDto actualDto = service.sendFriendRequest(senderId, receiverId);

        assertThat(actualDto.getFriendshipId(), is(dto.getFriendshipId()));
        assertThat(actualDto.getSenderId(), is(dto.getSenderId()));
        assertThat(actualDto.getReceiverId(), is(dto.getReceiverId()));
        assertThat(actualDto.getStatus(), is(dto.getStatus()));
    }

    @Test
    void testSendFriendRequest400WhenAlreadyFriends() {
        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);
        when(userRepository.findById(senderId))
                .thenReturn(Optional.ofNullable(sender));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.ofNullable(receiver));

        assertThrows(FriendException.class, () -> service.sendFriendRequest(senderId, receiverId));
    }

    @Test
    void testFindAllFriendsOkWhenValid() {
        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);
        when(userRepository.findById(senderId))
                .thenReturn(Optional.ofNullable(sender));

        List<UserDto> users = service.findAllFriends(senderId);
        UserDto actualUser = users.get(0);

        assertThat(actualUser.getId(), is(receiverId));
        assertThat(actualUser.getUsername(), is(receiver.getUsername()));
        assertThat(actualUser.getEmail(), is(receiver.getEmail()));
    }

    @Test
    void testApproveFriendshipOkWhenValid() {
        Friendship newFriendship = new Friendship();
        newFriendship.setId(friendship.getId());
        newFriendship.setSender(sender);
        newFriendship.setReceiver(receiver);
        newFriendship.setStatus(Status.APPROVED);
        dto.setStatus(Status.APPROVED);

        when(userRepository.findById(senderId))
                .thenReturn(Optional.ofNullable(sender));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.ofNullable(receiver));

        when(friendshipRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(friendship);
        when(friendshipRepository.save(any(Friendship.class)))
                .thenReturn(newFriendship);

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenReturn(subscription);

        FriendshipDto actualDto = service.approveFriendship(receiverId, senderId, true);

        assertThat(actualDto.getFriendshipId(), is(dto.getFriendshipId()));
        assertThat(actualDto.getSenderId(), is(dto.getSenderId()));
        assertThat(actualDto.getReceiverId(), is(dto.getReceiverId()));
        assertThat(actualDto.getStatus(), is(dto.getStatus()));
    }

    @Test
    void testApproveFriendship400WhenFriendshipIsNull() {
        User user1 = new User();
        user1.setId(11L);
        when(userRepository.findById(user1.getId()))
                .thenReturn(Optional.of(user1));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.ofNullable(receiver));
        when(friendshipRepository.findBySenderAndReceiver(user1, receiver))
                .thenReturn(null);

        assertThrows(FriendException.class, () -> service.approveFriendship(receiverId, user1.getId(), true));
    }

    @Test
    void testApproveFriendship400WhenFriendshipIsAlreadyApproved() {
        friendship.setStatus(Status.APPROVED);
        when(userRepository.findById(senderId))
                .thenReturn(Optional.ofNullable(sender));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.ofNullable(receiver));

        when(friendshipRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(friendship);

        assertThrows(FriendException.class, () -> service.approveFriendship(receiverId, senderId, true));
    }

    @Test
    void testApproveFriendshipOkWhenRejected() {
        Friendship newFriendship = new Friendship();
        newFriendship.setId(friendship.getId());
        newFriendship.setSender(sender);
        newFriendship.setReceiver(receiver);
        newFriendship.setStatus(Status.REJECTED);
        dto.setStatus(Status.REJECTED);

        when(userRepository.findById(senderId))
                .thenReturn(Optional.ofNullable(sender));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.ofNullable(receiver));

        when(friendshipRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(friendship);

        when(friendshipRepository.save(any(Friendship.class)))
                .thenReturn(newFriendship);

        FriendshipDto actualDto = service.approveFriendship(receiverId, senderId, false);

        assertThat(actualDto.getFriendshipId(), is(dto.getFriendshipId()));
        assertThat(actualDto.getSenderId(), is(dto.getSenderId()));
        assertThat(actualDto.getReceiverId(), is(dto.getReceiverId()));
        assertThat(actualDto.getStatus(), is(dto.getStatus()));
    }

    @Test
    void testDeleteFriendOkWhenValid() {
        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);
        friendship.setStatus(Status.APPROVED);

        when(userRepository.findById(senderId))
                .thenReturn(Optional.ofNullable(sender));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.ofNullable(receiver));
        when(friendshipRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(friendship);

        service.deleteFriend(senderId, receiverId);

        verify(friendshipRepository, times(1))
                .deleteBySenderAndReceiver(sender, receiver);
        verify(subscriptionRepository, times(1))
                .deleteBySubscriberAndPublisher(sender, receiver);
    }

    @Test
    void testDeleteFriendWhenSubscribed() {
        when(userRepository.findById(senderId))
                .thenReturn(Optional.ofNullable(sender));
        when(userRepository.findById(receiverId))
                .thenReturn(Optional.ofNullable(receiver));
        when(friendshipRepository.findBySenderAndReceiver(sender, receiver))
                .thenReturn(friendship);

        service.deleteFriend(senderId, receiverId);

        verify(subscriptionRepository, times(1))
                .deleteBySubscriberAndPublisher(sender, receiver);
    }
}