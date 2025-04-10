package com.infinity.ai.platform.entity.vote;

import com.infinity.ai.platform.entity.tweet.TweetCommentData;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class VoteData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成ID
    private Long id;

    private String content;

    private String yesConent;

    private String noContent;

    private int yesCount;

    private int noCount;

    private int roomId;

    //投票状态 0:未开始 1:进行中 2:已结束
    private int state;

    private int animationId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startAt;

    @OneToMany(mappedBy = "voteData", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<UserVoteData> userVoteDataSet;

    @Override
    public int hashCode() {
        return Objects.hash(id); // 只使用主键字段
    }
}
