package com.infinity.ai.login.repository;


import com.infinity.ai.login.model.User;
import com.infinity.ai.login.model.UserTwitterBind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserTwitterBindRepository extends JpaRepository<UserTwitterBind, Long> {

    @Query("select u from UserTwitterBind u where u.twitterUserId = :twitterUserId")
    UserTwitterBind queryUserTwitterBindByTwitterUserId(@Param("twitterUserId") String twitterUserId);

    @Query("select u from UserTwitterBind u where u.userId = :userId")
    UserTwitterBind queryUserTwitterBind(@Param("userId") String userId);
}
