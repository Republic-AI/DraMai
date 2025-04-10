package com.infinity.ai.platform.entity.tweet;

import com.infinity.common.msg.platform.npc.ChatDataVo;
import com.infinity.db.db.JsonConverter;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Data
public class TweetCommentData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成ID
    private Long id;

    private long tweetId;

    private String content;

    private String nickName;

    @Convert(converter = JsonConverter.class)
    @Column(name = "next_comment_id", columnDefinition = "TEXT")
    private List<Long> nextCommentId = new ArrayList<>();

    private long replyId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_data_id", referencedColumnName = "id")
    private TweetData tweetData;

    @Override
    public int hashCode() {
        return Objects.hash(id); // 只使用主键字段
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TweetCommentData other = (TweetCommentData) obj;
        return Objects.equals(id, other.id);
    }
}
