package com.infinity.ai.login.repository;


import com.infinity.ai.login.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.userId = :userId")
    User queryUser(String userId);
}
