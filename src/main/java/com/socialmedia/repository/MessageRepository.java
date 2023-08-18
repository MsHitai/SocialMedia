package com.socialmedia.repository;

import com.socialmedia.model.Message;
import com.socialmedia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllBySenderAndReceiver(User sender, User receiver);

}
