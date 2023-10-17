package com.socialmedia.repository;

import com.socialmedia.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u left join fetch u.roles where u.username = :username")
    Optional<User> findByUsername(String username);

    List<User> findAllByIdIn(List<Long> ids);
}
