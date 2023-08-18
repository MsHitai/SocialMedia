package com.socialmedia.repository;

import com.socialmedia.model.Subscription;
import com.socialmedia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    void deleteBySubscriberAndPublisher(User sender, User receiver);

    List<Subscription> findAllBySubscriber(User subscriber);
}
