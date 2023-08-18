package com.socialmedia.service;

import com.socialmedia.dto.PostDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubscriptionService {

    List<PostDto> findAllPostsByPublisher(Long userId, Pageable page);

}
