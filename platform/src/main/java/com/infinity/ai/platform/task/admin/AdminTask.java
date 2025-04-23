package com.infinity.ai.platform.task.admin;

import com.alibaba.fastjson.JSONObject;
import com.infinity.ai.platform.entity.tweet.TweetData;
import com.infinity.ai.platform.repository.TweetDataRepository;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.admin.AdminReponse;
import com.infinity.common.msg.platform.admin.AdminRequest;
import com.infinity.common.msg.platform.tweet.TweetVo;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;

import java.util.ArrayList;
import java.util.List;

public class AdminTask extends BaseTask<AdminRequest> {
    @Override
    public int getCommandID() {
        return ProtocolCommon.KAdminCommand;
    }

    @Override
    protected boolean run0() {
        AdminRequest request = this.getMsg();
        String cmd = request.getData().getCmd();
        switch (cmd) {
            case "getTweets": {
                getTweets();
                break;
            }
        }
        return true;
    }

    public void getTweets() {
        List<TweetData> tweetDataList = SpringContextHolder.getBean(TweetDataRepository.class).findAll();
        List<TweetVo> tweetVos = new ArrayList<>();
        for (TweetData tweetData : tweetDataList) {
            TweetVo tweetVo = new TweetVo();
            tweetVo.setId(tweetData.getId());
            tweetVo.setContent(tweetData.getContent());
            tweetVo.setImgUrl(tweetData.getImgUrl());
            tweetVo.setTweetType(tweetData.getTweetType());
            tweetVo.setNpcId(tweetData.getNpcId());
            tweetVo.setRoomId(tweetData.getRoomId());
            tweetVo.setChooseList(tweetData.getChooseList());
            tweetVos.add(tweetVo);
        }
        sendMessage(buildResponse(JSONObject.toJSONString(tweetVos)));
    }

    private BaseMsg buildResponse(String data) {
        AdminReponse response = new AdminReponse();
        response.setRequestId(msg.getRequestId());
        response.setSessionId(msg.getSessionId());
        //response.setCode(rt.first ? 0 : ProtocolCommon.MSG_ERROR);
        response.setCode(0);
        AdminReponse.ResponseData responseData = new AdminReponse.ResponseData();
        responseData.setContent(data);
        response.setData(responseData);
        return response;
    }
}
