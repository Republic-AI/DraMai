package com.infinity.ai.platform.task.npc;

import com.infinity.ai.platform.constant.VoteConstant;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.manager.RoomManager;
import com.infinity.ai.platform.npc.live.NpcRoom;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.consts.ActionStatus;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import com.infinity.common.msg.platform.npc.NpcActionResponse;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
import lombok.extern.slf4j.Slf4j;

/**
 * python发过来的NPC行为
 * from: python
 * dest: 服务端
 */
@Slf4j
public class NpcAtionTask extends BaseTask<NpcActionRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.NPC_ACTION_COMMAND;
    }

    @Override
    public boolean run0() {
        NpcActionRequest msg = this.getMsg();
        log.debug("NpcAtionTask msg={}", msg.toString());
        long npcId = msg.getNpcId();
        if (npcId <= 0) {
            log.error("NpcAtionTask error, npcId params error,npcId={}", npcId);
            sendMessage(buildError(ResultCode.NPCID_FORMAT_ERROR, msg));
            return false;
        }

        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(npcId);
        if (npcHolder == null) {
            log.debug("not found on line npc,npcId={}", npcId);
            sendMessage(buildError(ResultCode.NPC_NOT_EXIST_ERROR, msg));
            return false;
        }

        NpcActionResponse npcActionResponse = new NpcActionResponse();
        npcActionResponse.setNpcId(msg.getNpcId());
        npcActionResponse.setActionId(msg.getActionId());
        npcActionResponse.setAck(msg.getAck());
        npcActionResponse.setData(msg.getData());

        //执行行为
        try {
            NpcRoom npcRoom = RoomManager.getInstance().getRoom(npcHolder.getNpc().getRoomId());
            if (npcRoom.getState() == VoteConstant.VOTE_STATE_DRAMA) {
                log.debug("NpcAtionTask in drama, roomId = {}, npcId = {}", npcRoom.getRoomId(), npcId);
                return false;
            }
            npcHolder.getNpc().doAction(msg.getActionId(), msg, npcActionResponse, false);
            sendMessageToPython(npcActionResponse);
        } catch (Exception e) {
            log.error("NpcAtionTask 执行失败", e);
            npcHolder.getNpc().getNpcDataListener().notifyProperty(true);
            npcActionResponse.setStatus(ActionStatus.FAIL);
            sendMessageToPython(npcActionResponse);
        }
        return true;
    }

    public void sendMessageToPython(NpcActionResponse response) {
        response.setPlayerId(null);
        response.setGateway(null);
        response.setSessionId(null);
        response.setRequestId(RequestIDManager.getInstance().RequestID(false));
        log.debug("sendMessage NpcActionResponse ===================:{}", response.toString());
        MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kPythonService, response);
    }
}
