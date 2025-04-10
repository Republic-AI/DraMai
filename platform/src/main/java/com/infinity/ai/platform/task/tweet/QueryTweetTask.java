package com.infinity.ai.platform.task.tweet;

import com.infinity.ai.platform.entity.tweet.TweetChooseData;
import com.infinity.ai.platform.entity.tweet.TweetCommentData;
import com.infinity.ai.platform.entity.tweet.TweetData;
import com.infinity.ai.platform.entity.tweet.TweetLikeData;
import com.infinity.ai.platform.repository.TweetDataRepository;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.tweet.QueryTweetReponse;
import com.infinity.common.msg.platform.tweet.QueryTweetRequest;
import com.infinity.common.msg.platform.tweet.TweetCommentVo;
import com.infinity.common.msg.platform.tweet.TweetVo;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 切换NPC房间
 */
@Slf4j
public class QueryTweetTask extends BaseTask<QueryTweetRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.QUERY_TWEETS_COMMAND;
    }

    @Override
    public boolean run0() {
        QueryTweetRequest queryTweetRequest = getMsg();
        log.debug("QueryTweetTask, msg={}", msg.toString());

        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("QueryTweetTask error, playerId params error,playerId={}", playerId);
            return false;
        }
        int roomId = queryTweetRequest.getData().getRoomId();
        int page = queryTweetRequest.getData().getPage();
        int size = queryTweetRequest.getData().getSize();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<TweetData> tweetDataPage = SpringContextHolder.getBean(TweetDataRepository.class).findAll(pageable);

        List<TweetVo> tweetVos = new ArrayList<>();
        for (TweetData tweetData : tweetDataPage.getContent()) {
            TweetVo tweetVo = new TweetVo();
            tweetVo.setId(tweetData.getId());
            tweetVo.setContent(tweetData.getContent());
            tweetVo.setImgUrl(tweetData.getImgUrl());
            tweetVo.setTweetType(tweetData.getTweetType());
            tweetVo.setNpcId(tweetData.getNpcId());
            tweetVo.setRoomId(tweetData.getRoomId());
            tweetVo.setChooseList(tweetData.getChooseList());
            List<TweetCommentVo> tweetCommentVos = new ArrayList<>();
            for (TweetCommentData tweetCommentData : tweetData.getTweetCommentDataList()) {
                 if (tweetCommentData.getReplyId() != 0) {
                     continue;
                 }
                 TweetCommentVo tweetCommentVo = new TweetCommentVo();
                 tweetCommentVo.setId(tweetCommentData.getId());
                 tweetCommentVo.setContent(tweetCommentData.getContent());
                 tweetCommentVo.setNickName(tweetCommentData.getNickName());
                 tweetCommentVos.add(tweetCommentVo);
                 Map<Long, TweetCommentData> tweetCommentDataMap = new HashMap<>();
                 for (TweetCommentData temp : tweetData.getTweetCommentDataList()){
                     tweetCommentDataMap.put(temp.getId(), temp);
                 }
                for (Number commentIdNum : tweetCommentData.getNextCommentId()) {
                    long commentId = commentIdNum.longValue();
                    TweetCommentData temp = tweetCommentDataMap.get(commentId);
                    createTweetCommentVoList(tweetCommentVo, temp, tweetCommentDataMap);
                }
            }
            for (TweetLikeData tweetLikeData : tweetData.getTweetLikeDataList()) {
                if (playerId == tweetLikeData.getUserId()) {
                    tweetVo.setLike(true);
                    break;
                }
            }
            List<Integer> rateList = new ArrayList<>();
            int[] rateArr = new int[4];
            for (TweetChooseData tweetLikeData : tweetData.getTweetChooseDataList()) {
                if (playerId == tweetLikeData.getUserId()) {
                    tweetVo.setChoose(true);
                }
                rateArr[tweetLikeData.getChooseIndex()] ++;
            }
            int totalCount = tweetData.getTweetChooseDataList().size();
            for (Integer count : rateArr) {
                if (totalCount != 0 && count != 0) {
                    BigDecimal percentage = new BigDecimal(count).multiply(new BigDecimal(100)).divide(new BigDecimal(totalCount), 2, RoundingMode.HALF_UP);
                    rateList.add(percentage.intValue());
                } else {
                    rateList.add(0);
                }
            }
            tweetVo.setTweetCommentVoList(tweetCommentVos);
            tweetVo.setRateList(rateList);
            tweetVo.setCommentCount(tweetData.getTweetCommentDataList().size());
            tweetVo.setLikeCount(tweetData.getTweetLikeDataList().size());
            tweetVo.setCreateTime(System.currentTimeMillis() - tweetData.getCreatedAt().getTime());
            tweetVos.add(tweetVo);
        }
        QueryTweetReponse queryTweetReponse = new QueryTweetReponse();
        queryTweetReponse.setPlayerId(playerId);
        QueryTweetReponse.ResponseData responseData = new QueryTweetReponse.ResponseData();
        responseData.setTweetVoList(tweetVos);
        responseData.setRoomId(roomId);
        queryTweetReponse.setData(responseData);
        sendMessage(queryTweetReponse);
        return true;
    }

    public void createTweetCommentVoList(TweetCommentVo tweetCommentVo, TweetCommentData tweetCommentData, Map<Long, TweetCommentData> tweetCommentDataMap) {
        if (tweetCommentData == null) {
            return;
        }
        TweetCommentVo commentVo = new TweetCommentVo();
        commentVo.setId(tweetCommentData.getId());
        commentVo.setContent(tweetCommentData.getContent());
        commentVo.setNickName(tweetCommentData.getNickName());
        tweetCommentVo.getTweetCommentVo().add(commentVo);
        if (tweetCommentData.getNextCommentId().isEmpty()) {
            for (Number commentIdNum : tweetCommentData.getNextCommentId())  {
                long commentId = commentIdNum.longValue();
                TweetCommentData temp = tweetCommentDataMap.get(commentId);
                createTweetCommentVoList(commentVo, temp, tweetCommentDataMap);
            }
        }
    }
}
