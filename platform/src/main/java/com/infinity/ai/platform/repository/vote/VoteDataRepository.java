package com.infinity.ai.platform.repository.vote;


import com.infinity.ai.platform.entity.tweet.TweetData;
import com.infinity.ai.platform.entity.vote.VoteData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteDataRepository extends JpaRepository<VoteData, Long> {

    List<VoteData> findByRoomIdAndState(int room, int state);

    @Query("SELECT v FROM VoteData v WHERE v.roomId = :roomId ORDER BY v.id DESC")
    List<VoteData> findTopByRoomIdOrderByVoteIdDesc(@Param("roomId") int roomId);


    Page<VoteData> findByRoomIdAndState(int room, int state, Pageable pageable);
}
