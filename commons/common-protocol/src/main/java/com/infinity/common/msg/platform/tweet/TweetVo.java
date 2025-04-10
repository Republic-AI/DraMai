package com.infinity.common.msg.platform.tweet;

import lombok.Data;
import java.util.List;

@Data
public class TweetVo {

    private long id;

    private String content;

    private String imgUrl;

    private List<TweetCommentVo> tweetCommentVoList;

    private int commentCount;

    private int likeCount;

    private boolean like;

    private long createTime;

    private int tweetType;

    private long npcId;

    private int roomId;

    private List<String> chooseList;

    private boolean choose;

    private List<Integer> rateList;
}
