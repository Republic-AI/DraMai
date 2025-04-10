package com.infinity.ai.platform.task.tweet;

import com.infinity.ai.platform.constant.TweetConstant;
import com.infinity.ai.platform.entity.tweet.TweetChooseData;
import com.infinity.ai.platform.entity.tweet.TweetCommentData;
import com.infinity.ai.platform.entity.tweet.TweetData;
import com.infinity.ai.platform.entity.tweet.TweetLikeData;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.repository.TweetCommentDataRepository;
import com.infinity.ai.platform.repository.TweetDataRepository;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.tweet.OperateTweetReponse;
import com.infinity.common.msg.platform.tweet.OperateTweetRequest;
import com.infinity.common.msg.platform.tweet.QueryTweetRequest;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 切换NPC房间
 */
@Slf4j
public class OperateTweetTask extends BaseTask<OperateTweetRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.COMMENT_TWEET_COMMAND;
    }

    @Override
    public boolean run0() {
        OperateTweetRequest operateTweetRequest = getMsg();
        log.debug("OperateTweetTask, msg={}", msg.toString());
        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("OperateTweetTask error, playerId params error,playerId={}", playerId);
            return false;
        }
        Player player = PlayerManager.getInstance().getOnlinePlayer(playerId);
        long tweetId = operateTweetRequest.getData().getTweetId();
        int chooseIndex = operateTweetRequest.getData().getChooseIndex();
        TweetDataRepository tweetDataRepository = SpringContextHolder.getBean(TweetDataRepository.class);
        TweetData tweetData = tweetDataRepository.findById(tweetId);
        if (tweetData == null) {
            log.error("TweetData not found, playerId params error,playerId={}", playerId);
            return false;
        }
        long commentId = 0;
        int type = operateTweetRequest.getData().getType();
        List<Integer> rateList = new ArrayList<>();
        if (type == TweetConstant.OP_TYPE_LIKE) {
            Set<TweetLikeData> tweetLikeDataList = tweetData.getTweetLikeDataList();
            if (tweetLikeDataList == null) {
                tweetLikeDataList = new HashSet<>();
            }
            TweetLikeData temp = null;
            for (TweetLikeData tweetLikeData : tweetLikeDataList) {
                if (tweetLikeData.getUserId() == playerId) {
                    temp = tweetLikeData;
                    break;
                }
            }
            if (temp == null) {
                temp = new TweetLikeData();
                temp.setTweetId(tweetId);
                temp.setUserId(playerId);
                temp.setTweetData(tweetData);
                tweetLikeDataList.add(temp);
            } else {
                tweetLikeDataList.remove(temp);
            }
            tweetDataRepository.save(tweetData);
        } else if (type == TweetConstant.OP_TYPE_CHOOSE) {
            Set<TweetChooseData> tweetChooseDataList = tweetData.getTweetChooseDataList();
            TweetChooseData temp = null;
            for (TweetChooseData tweetChooseData : tweetChooseDataList) {
                if (tweetChooseData.getUserId() == playerId) {
                    temp = tweetChooseData;
                    break;
                }
            }
            if (temp == null) {
                temp = new TweetChooseData();
                temp.setTweetId(tweetId);
                temp.setUserId(playerId);
                temp.setChooseIndex(chooseIndex);
                temp.setTweetData(tweetData);
                tweetChooseDataList.add(temp);
            } else {
                tweetChooseDataList.remove(temp);
            }
            tweetDataRepository.save(tweetData);
            int[] rateArr = new int[4];
            for (TweetChooseData tweetLikeData : tweetData.getTweetChooseDataList()) {
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
        } else if (type == TweetConstant.OP_TYPE_COMMENT) {
            TweetCommentData tweetCommentData = new TweetCommentData();
            tweetCommentData.setContent(operateTweetRequest.getData().getContent());
            tweetCommentData.setNickName(player.getNickname());
            tweetCommentData.setTweetData(tweetData);
            tweetCommentData.setReplyId(operateTweetRequest.getData().getReplyId());
            //tweetData.getTweetCommentDataList().add(tweetCommentData);
            Set<TweetCommentData> tweetCommentDataList = tweetData.getTweetCommentDataList();
            SpringContextHolder.getBean(TweetCommentDataRepository.class).save(tweetCommentData);
            for (TweetCommentData temp : tweetCommentDataList) {
                if (temp.getId() != null && temp.getId() == operateTweetRequest.getData().getReplyId()) {
                    temp.getNextCommentId().add(tweetCommentData.getId());
                    SpringContextHolder.getBean(TweetCommentDataRepository.class).save(temp);
                    break;
                }
            }
            commentId = tweetCommentData.getId();
        } else {
            log.error("OperateTweetTask error, type params error,type={}", type);
            return false;
        }
        OperateTweetReponse operateTweetReponse = new OperateTweetReponse();
        operateTweetReponse.setPlayerId(playerId);
        OperateTweetReponse.ResponseData responseData = new OperateTweetReponse.ResponseData();
        operateTweetReponse.setData(responseData);
        responseData.setTweetId(tweetId);
        responseData.setContent(operateTweetRequest.getData().getContent());
        responseData.setType(type);
        responseData.setNickName(player.getNickname());
        responseData.setReplyId(operateTweetRequest.getData().getReplyId());
        responseData.setCommentId(commentId);
        responseData.setUserNo(player.getPlayerModel().getUserno());
        responseData.setChooseIndex(chooseIndex);
        responseData.setRateList(rateList);
        BroadcastMesage.getInstance().send(playerId, operateTweetReponse.toString());
        return true;
    }


}
