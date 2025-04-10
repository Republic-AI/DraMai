package com.infinity.ai.platform.entity.vote;

import com.infinity.ai.platform.entity.tweet.TweetData;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
@Table(name = "user_vote_data", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"vote_data_id", "userId"})
})
public class UserVoteData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成ID
    private Long id;

    private Long userId;

    private int yesCount;

    private int noCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_data_id", referencedColumnName = "id")
    private VoteData voteData;

    @Override
    public int hashCode() {
        return Objects.hash(id); // 只使用主键字段
    }
}
