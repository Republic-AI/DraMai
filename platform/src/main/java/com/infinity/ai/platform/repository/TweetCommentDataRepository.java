package com.infinity.ai.platform.repository;


import com.infinity.ai.platform.entity.tweet.TweetCommentData;
import com.infinity.ai.platform.entity.tweet.TweetData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetCommentDataRepository extends JpaRepository<TweetCommentData, Long> {
}