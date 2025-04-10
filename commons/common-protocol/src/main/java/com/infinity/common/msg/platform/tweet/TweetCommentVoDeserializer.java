package com.infinity.common.msg.platform.tweet;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TweetCommentVoDeserializer extends JsonDeserializer<TweetCommentVo> {
    @Override
    public TweetCommentVo deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        TweetCommentVo comment = new TweetCommentVo();
        comment.setId(node.get("id").asLong());
        comment.setContent(node.get("content").asText());
        comment.setNickName(node.get("nickName").asText());

        if (node.has("tweetCommentVo")) {
            List<TweetCommentVo> replies = new ArrayList<>();
            for (JsonNode replyNode : node.get("tweetCommentVo")) {
                replies.add(jsonParser.getCodec().treeToValue(replyNode, TweetCommentVo.class));
            }
            comment.setTweetCommentVo(replies);
        }
        return comment;
    }
}