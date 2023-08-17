package com.socialmedia.repository;

import com.socialmedia.model.Friendship;
import com.socialmedia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    List<Friendship> findAllBySender(User sender);

    Friendship findBySenderAndReceiver(User sender, User receiver);

    void deleteBySenderAndReceiver(User sender, User receiver);

}
