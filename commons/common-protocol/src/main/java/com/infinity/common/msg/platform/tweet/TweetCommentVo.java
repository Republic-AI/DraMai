package com.infinity.common.msg.platform.tweet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonDeserialize(using = TweetCommentVoDeserializer.class)
public class TweetCommentVo {

    private long id;

    private String content;

    private String nickName;

    @JsonProperty("tweetCommentVo")
    private List<TweetCommentVo> tweetCommentVo = new ArrayList<>();
}
