package com.infinity.ai.platform.entity.tweet;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
@Data
public class TweetChooseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成ID
    private Long id;

    private long tweetId;

    private long userId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_data_id", referencedColumnName = "id")
    private TweetData tweetData;

    private int chooseIndex;

    @Override
    public int hashCode() {
        return Objects.hash(id); // 只使用主键字段
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TweetChooseData other = (TweetChooseData) obj;
        return Objects.equals(id, other.id);
    }
}
