package com.infinity.ai.platform.task.admin;

import com.alibaba.fastjson.JSONObject;
import com.infinity.ai.domain.tables.VMap;
import com.infinity.ai.platform.constant.TweetConstant;
import com.infinity.ai.platform.entity.tweet.TweetData;
import com.infinity.ai.platform.entity.vo.BannerVo;
import com.infinity.ai.platform.entity.vo.VoteVo;
import com.infinity.ai.platform.entity.vote.VoteData;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.repository.TweetDataRepository;
import com.infinity.ai.platform.repository.vote.VoteDataRepository;
import com.infinity.ai.platform.task.npc.PlayerNpcSetTask;
import com.infinity.common.config.data.RelationCfg;
import com.infinity.common.config.data.RoomCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.config.manager.RoomCfgManager;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.admin.AdminReponse;
import com.infinity.common.msg.platform.admin.AdminRequest;
import com.infinity.common.msg.platform.npc.NpcData;
import com.infinity.common.msg.platform.room.RelationData;
import com.infinity.common.msg.platform.tweet.TweetVo;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AdminTask extends BaseTask<AdminRequest> {
    @Override
    public int getCommandID() {
        return ProtocolCommon.KAdminCommand;
    }

    @Override
    protected boolean run0() {
        AdminRequest request = this.getMsg();
        String cmd = request.getData().getCmd();
        String content = request.getData().getContent();
        switch (cmd) {
            case "getTweets": {
                getTweets();
                break;
            }
            case "getNpcs": {
                getNpcs();
                break;
            }
            case "addTweet": {
                addTweet(content);
                break;
            }
            case "delTweet": {
                delTweet(content);
                break;
            }
            case "getBanners": {
                getBanners();
                break;
            }
            case "updateBanner": {
                updateBanner(content);
                break;
            }
            case "getVotes": {
                getVotes();
                break;
            }
            case "delVote": {
                delVote(content);
                break;
            }
            case "addVote": {
                addVote(content);
                break;
            }
            case "getRelations": {
                getRelations();
                break;
            }
            case "updateRelation": {
                updateRelation(content);
                break;
            }
        }
        return true;
    }

    private void getRelations() {
        VMap vMap = MapDataManager.getInstance().getMap().get_v();
        List<RelationData> relationDataList = new ArrayList<>();
        List<RelationCfg> list = GameConfigManager.getInstance().getRelationCfgManager().getAllRelationCfg();
        Map<String, String> relationMap = vMap.getRelationConfig();
        for (RelationCfg relationCfg : list) {
            String relation = relationCfg.getRelation();
            String key = relationCfg.getNpcId1() + "_" + relationCfg.getNpcId2();
            if (relationMap.containsKey(key)) {
                relation = relationMap.get(key);
            }
            RelationData data = new RelationData();
            data.setNpcId1(relationCfg.getNpcId1());
            data.setNpcId2(relationCfg.getNpcId2());
            data.setRelation(relation);
            relationDataList.add(data);
        }
        sendMessage(buildResponse(JSONObject.toJSONString(relationDataList)));
    }

    private void getVotes() {
        List<VoteData> voteDataList = SpringContextHolder.getBean(VoteDataRepository.class).findAll();
        List<VoteVo> voteVos = new ArrayList<>();
        for (VoteData voteData : voteDataList) {
            VoteVo voteVo = new VoteVo();
            voteVo.setId(voteData.getId());
            voteVo.setContent(voteData.getContent());
            voteVo.setYesConent(voteData.getYesConent());
            voteVo.setNoContent(voteData.getNoContent());
            voteVo.setYesCount(voteData.getYesCount());
            voteVo.setNoCount(voteData.getNoCount());
            voteVo.setRoomId(voteData.getRoomId());
            voteVo.setState(voteData.getState());
            voteVo.setAnimationId(voteData.getAnimationId());
            voteVos.add(voteVo);
        }
        sendMessage(buildResponse(JSONObject.toJSONString(voteVos)));
    }

    private void updateBanner(String content) {
        BannerVo bannerVo = JSONObject.parseObject(content, BannerVo.class);
        MapDataManager.getInstance().getMap().get_v().getBannerConfig().put(bannerVo.getRoomId(), bannerVo.getImgUrl());
        sendMessage(buildResponse(JSONObject.toJSONString(bannerVo)));
    }

    private void updateRelation(String content) {
        RelationData relationData = JSONObject.parseObject(content, RelationData.class);
        MapDataManager.getInstance().getMap().get_v().getRelationConfig().put(relationData.getNpcId1() + "_" + relationData.getNpcId2(), relationData.getRelation());
        sendMessage(buildResponse(JSONObject.toJSONString(relationData)));
    }

    private void getBanners() {
        List<BannerVo> banners = new ArrayList<>();
        RoomCfgManager roomCfgManager = GameConfigManager.getInstance().getRoomCfgManager();
        VMap vMap = MapDataManager.getInstance().getMap().get_v();
        for (RoomCfg roomCfg : roomCfgManager.getAllJoinCfg()) {
            BannerVo bannerVO = new BannerVo();
            bannerVO.setRoomId(roomCfg.getId());
            if (vMap.getBannerConfig().containsKey(roomCfg.getId())) {
                bannerVO.setImgUrl(vMap.getBannerConfig().get(roomCfg.getId()));
            } else {
                bannerVO.setImgUrl(roomCfg.getBanner());
            }
            banners.add(bannerVO);
        }
        sendMessage(buildResponse(JSONObject.toJSONString(banners)));
    }


    private void addTweet(String content) {
        TweetVo tweetVo = JSONObject.parseObject(content, TweetVo.class);
        TweetData tweetData = new TweetData();
        tweetData.setContent(tweetVo.getContent());
        tweetData.setNpcId(tweetVo.getNpcId());
        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(tweetVo.getNpcId());
        tweetData.setRoomId(npcHolder.getNpc().getRoomId());
        tweetData.setChooseList(tweetVo.getChooseList());
        tweetData.setTweetType(tweetVo.getTweetType());
        tweetData.setImgUrl(tweetVo.getImgUrl());
        tweetData.setVideoUrl(tweetVo.getVideoUrl());
        tweetData.setCreatedAt(new Date());
        int type = TweetConstant.TWEET_TYPE_TEXT;
        for (String choose : tweetVo.getChooseList()) {
            if (StringUtils.isNotEmpty(choose)) {
                type = TweetConstant.TWEET_TYPE_CHOOSE;
            }
        }
        tweetData.setTweetType(type);
        SpringContextHolder.getBean(TweetDataRepository.class).save(tweetData);
        sendMessage(buildResponse(JSONObject.toJSONString(tweetVo)));
    }

    private void delTweet(String content) {
        SpringContextHolder.getBean(TweetDataRepository.class).deleteAllById(List.of(Long.parseLong(content)));
        sendMessage(buildResponse(content));
    }

    private void delVote(String content) {
        SpringContextHolder.getBean(VoteDataRepository.class).deleteAllById(List.of(Long.parseLong(content)));
        sendMessage(buildResponse(content));
    }

    private void addVote(String content) {
        VoteVo voteVo = JSONObject.parseObject(content, VoteVo.class);
        VoteData voteData = new VoteData();
        voteData.setContent(voteVo.getContent());
        voteData.setYesConent(voteVo.getYesConent());
        voteData.setNoContent(voteVo.getNoContent());
        voteData.setAnimationId(voteVo.getAnimationId());
        SpringContextHolder.getBean(VoteDataRepository.class).save(voteData);
        sendMessage(buildResponse(JSONObject.toJSONString(voteVo)));
    }

    public void getTweets() {
        List<TweetData> tweetDataList = SpringContextHolder.getBean(TweetDataRepository.class).findAll();
        List<TweetVo> tweetVos = new ArrayList<>();
        for (TweetData tweetData : tweetDataList) {
            TweetVo tweetVo = new TweetVo();
            tweetVo.setId(tweetData.getId());
            tweetVo.setContent(tweetData.getContent());
            tweetVo.setImgUrl(tweetData.getImgUrl());
            tweetVo.setVideoUrl(tweetData.getVideoUrl());
            tweetVo.setTweetType(tweetData.getTweetType());
            tweetVo.setNpcId(tweetData.getNpcId());
            tweetVo.setRoomId(tweetData.getRoomId());
            tweetVo.setChooseList(tweetData.getChooseList());
            tweetVos.add(tweetVo);
        }
        sendMessage(buildResponse(JSONObject.toJSONString(tweetVos)));
    }

    public void getNpcs() {
        List<NpcData> npcs = new ArrayList<>();
        for (NpcHolder entry : NpcManager.getInstance().getOnlineNpcMap().values()) {
            NpcData myNpc = PlayerNpcSetTask.buildNpcData(entry.getNpc(), entry.getNpcModel());
            npcs.add(myNpc);
        }
        sendMessage(buildResponse(JSONObject.toJSONString(npcs)));
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
