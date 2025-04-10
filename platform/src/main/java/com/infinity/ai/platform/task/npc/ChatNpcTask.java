package com.infinity.ai.platform.task.npc;

import com.infinity.ai.platform.entity.NpcPlayerChatData;
import com.infinity.ai.platform.repository.NpcPlayerChatDataRepository;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.chat.PChatNpcRequest;
import com.infinity.common.msg.platform.npc.NpcReplyChatResponse;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

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
        Threads.runAsync(ThreadConst.QUEUE_LOGIC, "Async#saveChat", () -> {
            NpcPlayerChatData chatData = new NpcPlayerChatData();
            chatData.setNpcId(msg.getData().getNpcId());
            chatData.setPlayerId(playerId);
            chatData.setContent(msg.getData().getChatData().getContent());
            chatData.setNpcSend(false);
            chatData.setCreatedAt(new Date());
            SpringContextHolder.getBean(NpcPlayerChatDataRepository.class).save(chatData);
        });
        return false;
    }
}
