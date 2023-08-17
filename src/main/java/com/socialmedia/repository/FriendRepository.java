package com.socialmedia.repository;

import com.socialmedia.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    void deleteByFriendId(Long friendId);

}
