package com.infinity.ai.platform.task.vote;

import com.infinity.ai.platform.constant.VoteConstant;
import com.infinity.ai.platform.entity.vote.UserVoteData;
import com.infinity.ai.platform.entity.vote.VoteData;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.manager.RoomManager;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.ai.platform.repository.vote.VoteDataRepository;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.vote.QueryVoteRequest;
import com.infinity.common.msg.platform.vote.QueryVoteResponse;
import com.infinity.common.msg.platform.vote.VoteHistoryRequest;
import com.infinity.common.msg.platform.vote.VoteHistoryResponse;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VoteHistoryTask extends BaseTask<VoteHistoryRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.VOTE_HISTORY_COMMAND;
    }

    @Override
    public boolean run0() {
        VoteHistoryRequest msg = this.getMsg();
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
        VoteHistoryResponse queryVoteResponse = new VoteHistoryResponse();
        queryVoteResponse.setPlayerId(playerId);
        VoteHistoryResponse.ResponseData responseData = new VoteHistoryResponse.ResponseData();
        Pageable pageable = PageRequest.of(0, 50, Sort.by(Sort.Order.desc("id")));
        List<VoteData> voteDataList =  SpringContextHolder.getBean(VoteDataRepository.class).findByRoomIdAndState(roomId, VoteConstant.VOTE_STATE_END, pageable).getContent();
        List<VoteHistoryResponse.VoteHistoryInfo> voteHistoryInfoList = new ArrayList<>();
        for (VoteData voteData : voteDataList) {
            UserVoteData userVoteData = null;
            for (UserVoteData userVoteData1 : voteData.getUserVoteDataSet()) {
                if (userVoteData1.getUserId() == playerId) {
                    userVoteData = userVoteData1;
                }
            }
            VoteHistoryResponse.VoteHistoryInfo voteHistoryInfo = new VoteHistoryResponse.VoteHistoryInfo();
            voteHistoryInfo.setContent(voteData.getContent());
            voteHistoryInfo.setYesCount(voteData.getYesCount());
            voteHistoryInfo.setNoCount(voteData.getNoCount());
            if (userVoteData != null) {
                voteHistoryInfo.setMyYesCount(userVoteData.getYesCount());
                voteHistoryInfo.setMyNoCount(userVoteData.getNoCount());
            }
            voteHistoryInfoList.add(voteHistoryInfo);
        }
        responseData.setVoteHistoryInfoList(voteHistoryInfoList);
        queryVoteResponse.setData(responseData);
        sendMessage(queryVoteResponse);
        return false;
    }


}
