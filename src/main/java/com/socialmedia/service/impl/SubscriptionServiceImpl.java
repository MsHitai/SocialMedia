package com.socialmedia.service.impl;

import com.socialmedia.dto.PostDto;
import com.socialmedia.exceptions.DataNotFoundException;
import com.socialmedia.mapper.PostMapper;
import com.socialmedia.model.Post;
import com.socialmedia.model.Subscription;
import com.socialmedia.model.User;
import com.socialmedia.repository.PostRepository;
import com.socialmedia.repository.SubscriptionRepository;
import com.socialmedia.repository.UserRepository;
import com.socialmedia.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    public List<PostDto> findAllPostsByPublisher(Long userId, Pageable page) {
        User subscriber = checkUserId(userId);
        List<Subscription> subs = subscriptionRepository.findAllBySubscriber(subscriber);
        List<Long> publishersIds = subs.stream().map(sub -> sub.getPublisher().getId()).toList();
        List<User> publishers = userRepository.findAllByIdIn(publishersIds);
        List<Post> posts = postRepository.findByAuthorInOrderByCreatedDesc(publishers, page);
        return posts.stream().map(PostMapper::mapToPostDto).collect(Collectors.toList());
    }

    private User checkUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Пользователь по id " +
                userId + " не найден в базе данных"));
    }
}
