package com.infinity.ai.platform.entity.tweet;

import com.infinity.db.db.JsonConverter;
import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
public class TweetData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自动生成ID
    private Long id;

    private int roomId;

    private long npcId;

    private String content;

    private String imgUrl;

    private String videoUrl;

    @Convert(converter = JsonConverter.class)
    @Column(name = "choose_list", columnDefinition = "TEXT")
    private List<String> chooseList;

    private int tweetType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @OneToMany(mappedBy = "tweetData", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TweetCommentData> tweetCommentDataList;

    @OneToMany(mappedBy = "tweetData", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TweetLikeData> tweetLikeDataList;

    @OneToMany(mappedBy = "tweetData", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TweetChooseData> tweetChooseDataList;

    @Override
    public int hashCode() {
        return Objects.hash(id); // 只使用主键字段
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TweetData other = (TweetData) obj;
        return Objects.equals(id, other.id);
    }
}
