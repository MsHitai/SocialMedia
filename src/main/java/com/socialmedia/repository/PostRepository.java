package com.socialmedia.repository;

import com.socialmedia.model.Post;
import com.socialmedia.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorInOrderByCreatedDesc(List<User> authors, Pageable page);
}
