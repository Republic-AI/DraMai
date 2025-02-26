package com.infinity.ai.platform.task.npc;

import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.chat.PChatNpcRequest;
import com.infinity.common.msg.platform.npc.NpcReplyChatResponse;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChatNpcTask extends BaseTask<PChatNpcRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.P_NPC_TALK_COMMAND;
    }

    @Override
    public boolean run0() {
        PChatNpcRequest msg = this.getMsg();
        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("ChatNpcTask error, playerId params error,playerId={}", playerId);
            return true;
        }
        NpcReplyChatResponse request = new NpcReplyChatResponse();
        NpcReplyChatResponse.RequestData data = new NpcReplyChatResponse.RequestData();
        data.setChatData(msg.getData().getChatData());
        data.setNpcId(msg.getData().getNpcId());
        request.setPlayerId(null);
        request.setGateway(null);
        request.setSessionId(null);
        request.setRequestId(RequestIDManager.getInstance().RequestID(false));
        request.setData(data);
        log.debug("sendChatReplyMessage===================npcId = {},:{}",msg.getData().getNpcId(), request.toString());
        MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kPythonService, request);
        return false;
    }
}
