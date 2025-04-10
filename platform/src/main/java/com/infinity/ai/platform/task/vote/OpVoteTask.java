package com.infinity.ai.platform.task.vote;

import com.infinity.ai.platform.constant.VoteConstant;
import com.infinity.ai.platform.entity.vote.UserVoteData;
import com.infinity.ai.platform.entity.vote.VoteData;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.manager.RoomManager;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.ai.platform.repository.vote.UserVoteDataRepository;
import com.infinity.ai.platform.repository.vote.VoteDataRepository;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.vote.*;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class OpVoteTask extends BaseTask<OpVoteRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.OP_VOTE_COMMAND;
    }

    @Override
    public synchronized boolean run0() {
        OpVoteRequest msg = this.getMsg();
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
        if (npcRoom.getState() != VoteConstant.VOTE_STATE_NORMAL) {
            return false;
        }
        if (npcRoom == null) {
            sendErrorMsg(ErrorCode.SystemError, ErrorCode.SystemErrorMessage, msg);
            return false;
        }
        List<VoteData> voteDataList = SpringContextHolder.getBean(VoteDataRepository.class).findByRoomIdAndState(roomId, VoteConstant.VOTE_STATE_NORMAL);
        VoteData voteData = !voteDataList.isEmpty() ? voteDataList.get(0) : null;
        if (voteData == null) {
            log.debug("QueryVoteTask VoteData is null,request msg={}", msg);
            return false;
        }
        boolean choose = msg.getData().isChoose();
        int reward = msg.getData().getReward();
        UserVoteData userVoteData = null;
        for (UserVoteData userVoteData1 : voteData.getUserVoteDataSet()) {
            if (userVoteData1.getUserId() == playerId) {
                userVoteData = userVoteData1;
            }
        }
        if (userVoteData == null) {
            userVoteData = new UserVoteData();
            userVoteData.setUserId(playerId);
            userVoteData.setVoteData(voteData);
            voteData.getUserVoteDataSet().add(userVoteData);
        }
        if (userVoteData.getYesCount() == 0 && userVoteData.getNoCount() == 0 && reward == 0) {
            reward = 1;
        }
        if (choose) {
            userVoteData.setYesCount(userVoteData.getYesCount() + reward);
            voteData.setYesCount(voteData.getYesCount() + reward);
        } else {
            userVoteData.setNoCount(userVoteData.getNoCount() + reward);
            voteData.setNoCount(voteData.getNoCount() + reward);
        }
        SpringContextHolder.getBean(VoteDataRepository.class).save(voteData);
        OpVoteResponse opVoteResponse = new OpVoteResponse();
        opVoteResponse.setPlayerId(playerId);
        OpVoteResponse.ResponseData responseData = new OpVoteResponse.ResponseData();
        opVoteResponse.setData(responseData);
        responseData.setRoomId(roomId);
        responseData.setChoose(choose);
        responseData.setReward(reward);
        sendMessage(opVoteResponse);
        for (Long playerId : npcRoom.getPlayerList()) {
            NotifyVoteResponse notifyVoteResponse = buildResponse(voteData);
            notifyVoteResponse.setPlayerId(playerId);
            GameUser gameUser = GameUserMgr.getGameUser(playerId);
            log.debug("send msg -> {},{}", gameUser, notifyVoteResponse);
            if (gameUser != null) {
                MessageSender.getInstance().sendMessage(gameUser.getGatewayServiceId(), notifyVoteResponse);
            }
        }
        return false;
    }

    public NotifyVoteResponse buildResponse(VoteData voteData) {
        NotifyVoteResponse notifyVoteResponse = new NotifyVoteResponse();
        NotifyVoteResponse.ResponseData responseData = new NotifyVoteResponse.ResponseData();
        notifyVoteResponse.setData(responseData);
        responseData.setRoomId(voteData.getRoomId());
        responseData.setState(voteData.getState());
        responseData.setEndTime(voteData.getStartAt().getTime() + VoteConstant.DURATION_TIME - System.currentTimeMillis());
        responseData.setContent(voteData.getContent());
        responseData.setYesContent(voteData.getYesConent());
        responseData.setNoContent(voteData.getNoContent());
        responseData.setYesCount(voteData.getYesCount());
        responseData.setNoCount(voteData.getNoCount());
        return notifyVoteResponse;
    }

}
