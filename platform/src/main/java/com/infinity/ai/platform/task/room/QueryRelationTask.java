package com.infinity.ai.platform.task.room;

import com.infinity.ai.platform.constant.VoteConstant;
import com.infinity.ai.platform.entity.vote.UserVoteData;
import com.infinity.ai.platform.entity.vote.VoteData;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.manager.RoomManager;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.ai.platform.repository.vote.VoteDataRepository;
import com.infinity.common.config.data.RelationCfg;
import com.infinity.common.config.manager.GameConfigManager;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.room.QueryRelationRequest;
import com.infinity.common.msg.platform.room.QueryRelationResponse;
import com.infinity.common.msg.platform.room.RelationData;
import com.infinity.common.msg.platform.vote.QueryVoteRequest;
import com.infinity.common.msg.platform.vote.QueryVoteResponse;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class QueryRelationTask extends BaseTask<QueryRelationRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.QUERY_RELATION_COMMAND;
    }

    @Override
    public boolean run0() {
        QueryRelationRequest msg = this.getMsg();
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
        List<RelationData> relationDataList = new ArrayList<>();
        List<RelationCfg> list = GameConfigManager.getInstance().getRelationCfgManager().getAllRelationCfg();
        for (long npcId : npcRoom.getNpcList()) {
            for (RelationCfg relationCfg : list) {
                if (relationCfg.getNpcId1() == npcId) {
                    RelationData data = new RelationData();
                    data.setNpcId1(relationCfg.getNpcId1());
                    data.setNpcId2(relationCfg.getNpcId2());
                    data.setRelation(relationCfg.getRelation());
                    relationDataList.add(data);
                }
            }
        }
        QueryRelationResponse queryRelationResponse = new QueryRelationResponse();
        queryRelationResponse.setPlayerId(playerId);
        QueryRelationResponse.ResponseData responseData = new QueryRelationResponse.ResponseData();
        queryRelationResponse.setData(responseData);
        responseData.setRelationDataList(relationDataList);
        sendMessage(queryRelationResponse);
        return false;
    }


}
