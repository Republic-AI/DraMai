package com.infinity.ai.platform.task.vote;

import com.infinity.ai.platform.constant.VoteConstant;
import com.infinity.ai.platform.entity.vote.UserVoteData;
import com.infinity.ai.platform.entity.vote.VoteData;
import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.ai.platform.repository.vote.VoteDataRepository;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.consts.GoodsConsts;
import com.infinity.common.consts.GoodsSource;
import com.infinity.common.consts.SysParamsConsts;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcCommandRequest;
import com.infinity.common.msg.platform.npc.NpcCommandResponse;
import com.infinity.common.msg.platform.vote.QueryVoteRequest;
import com.infinity.common.msg.platform.vote.QueryVoteResponse;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class QueryVoteTask extends BaseTask<QueryVoteRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.QUERY_VOTE_COMMAND;
    }

    @Override
    public boolean run0() {
        QueryVoteRequest msg = this.getMsg();
        if (msg.getPlayerId() <= 0) {
            log.debug("QueryVoteTask playerId is error,request msg={}", msg);
            return false;
        }
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            return false;
        }
        int roomId = msg.getData().getRoomId();
        NpcRoom npcRoom = RoomManager.getInstance().getRoom(roomId);
        if (npcRoom == null) {
            sendErrorMsg(ErrorCode.SystemError, ErrorCode.SystemErrorMessage, msg);
            return false;
        }
        List<VoteData> voteDataList =  SpringContextHolder.getBean(VoteDataRepository.class).findTopByRoomIdOrderByVoteIdDesc(roomId);
        VoteData voteData = !voteDataList.isEmpty() ? voteDataList.get(0) : null;
        if (voteData == null) {
            log.debug("QueryVoteTask VoteData is null,request msg={}", msg);
            return false;
        }
        UserVoteData userVoteData = null;
        for (UserVoteData userVoteData1 : voteData.getUserVoteDataSet()) {
            if (userVoteData1.getUserId() == playerId) {
                userVoteData = userVoteData1;
            }
        }
        QueryVoteResponse queryVoteResponse = new QueryVoteResponse();
        queryVoteResponse.setPlayerId(playerId);
        QueryVoteResponse.ResponseData responseData = new QueryVoteResponse.ResponseData();
        queryVoteResponse.setData(responseData);
        responseData.setRoomId(roomId);
        responseData.setEndTime(voteData.getStartAt().getTime() + VoteConstant.DURATION_TIME - System.currentTimeMillis());
        responseData.setContent(voteData.getContent());
        responseData.setYesContent(voteData.getYesConent());
        responseData.setNoContent(voteData.getNoContent());
        responseData.setYesCount(voteData.getYesCount());
        responseData.setNoCount(voteData.getNoCount());
        responseData.setState(voteData.getState());
        if (userVoteData != null) {
            responseData.setMyYesCount(userVoteData.getYesCount());
            responseData.setMyNoCount(userVoteData.getNoCount());
        }
        sendMessage(queryVoteResponse);
        return false;
    }


}
