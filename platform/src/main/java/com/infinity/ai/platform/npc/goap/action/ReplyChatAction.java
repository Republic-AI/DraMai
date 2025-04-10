package com.infinity.ai.platform.npc.goap.action;

import com.infinity.ai.domain.model.ActionData;
import com.infinity.ai.platform.entity.NpcPlayerChatData;
import com.infinity.ai.platform.entity.NpcSpeakData;
import com.infinity.ai.platform.manager.MapDataManager;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.data.ReplyChatData;
import com.infinity.ai.platform.repository.NpcPlayerChatDataRepository;
import com.infinity.ai.platform.repository.NpcSpeakDataRepository;
import com.infinity.ai.platform.task.system.BroadcastMesage;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.msg.chat.ChatRequest;
import com.infinity.common.msg.platform.npc.NpcActionBroadRequest;
import com.infinity.common.msg.platform.npc.NpcActionRequest;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.node.NodeConstant;
import com.infinity.network.Channel;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 回复聊天行动类
@Slf4j
public class ReplyChatAction extends Action<NpcActionRequest.ReplyChatData> {

    public ReplyChatAction(Map<Integer, Action> preActions) {
        super(preActions);
    }

    @Override
    public String content(NPC npc, NpcActionRequest.ReplyChatData params) {
        return ReplyChatData.builder().content(params.getContent()).chatData(params.getChatData()).build().toJsonString();
    }

    public ReplyChatAction(Map<String, Boolean> preconditions, Map<String, Boolean> effects, int cost) {
        super(preconditions, effects, cost);
    }

    @Override
    public ActionEnumType getActionType() {
        return ActionEnumType.ReplyChat;
    }

    @Override
    public boolean canDoing(NPC npc, NpcActionRequest.ReplyChatData params) {
        return true;
    }

    @Override
    public void perform(NPC npc, ActionData actionData, NpcActionRequest.ReplyChatData params) {

    }

    @Override
    public void firstPerform(NPC npc, ActionData actionData, NpcActionRequest.ReplyChatData params) {
        //ChatRequest request = new ChatRequest();
        //request.setPlayerId(null);
        //request.setGateway(null);
        //request.setSessionId(null);
        //request.setRequestId(RequestIDManager.getInstance().RequestID(false));
        ChatRequest.RequestData data = new ChatRequest.RequestData();
        data.setType(0);
        data.setNpcId(npc.getId());
        data.setSender(String.valueOf(npc.getId()));
        data.setContext(params.getContent());
        data.setReceiver(params.getChatData().getSender());
        data.setRName(params.getChatData().getSname());
        data.setReplyMsgId(params.getChatData().getMsgId());
        data.setPrivateMsg(params.getChatData().isPrivateMsg());
        //request.setData(data);
        //log.debug("sendMessage===================:{}",request.toString());
        //MessageSender.getInstance().sendMessage(NodeConstant.kChatService, request);
        if (params.getChatData().isPrivateMsg()) {
            GameUser gameUser = GameUserMgr.getGameUser(params.getChatData().getSender());
            if (gameUser != null) {
                sendMessage(gameUser, npc, actionData, gameUser.getUserId(), "chatData", data);
                Threads.runAsync(ThreadConst.QUEUE_LOGIC, "Async#saveChat", () -> {
                    NpcPlayerChatData chatData = new NpcPlayerChatData();
                    chatData.setNpcId(npc.getId());
                    chatData.setPlayerId(gameUser.getUserId());
                    chatData.setContent(params.getContent());
                    chatData.setNpcSend(true);
                    chatData.setCreatedAt(new Date());
                    SpringContextHolder.getBean(NpcPlayerChatDataRepository.class).save(chatData);
                });
            }
        } else {
            sendMessage(npc, actionData, null,"chatData", data);
        }
    }

    public void sendMessage(GameUser gameUser, NPC npc, ActionData actionData, long userId, Object... params) {
        NpcActionBroadRequest request = new NpcActionBroadRequest();
        NpcActionBroadRequest.RequestData data = new NpcActionBroadRequest.RequestData();
        data.setNpcId(npc.getId());
        data.setActionId(this.getActionType().getCode());
        if (actionData != null) {
            data.setStartTime(actionData.getStartTime());
            data.setEndTime(actionData.getEndTime());
        }
        //data.setUsers(getUserIds(3));

        Map<String, Object> outParams = new HashMap<>();
        if (params != null) {
            int length = params.length;
            if (length % 2 != 0) {
                throw new BusinessException("参数格式有误");
            }

            for (int i = 0; i < length; i += 2) {
                outParams.put((String) params[i], params[i + 1]);
            }
        }

        data.setParams(outParams);
        request.setPlayerId(userId);
        request.setData(data);
        log.debug("sendMessage,msg={}", request.toString());
        MessageSender.getInstance().sendMessage(gameUser.getGatewayServiceId(), request);
    }

    @Override
    public void afterPerform(NPC npc, ActionData params) {
        //同步当前行为给python
        //npc.getNpcDataListener().notifyProperty(true);
    }

}


