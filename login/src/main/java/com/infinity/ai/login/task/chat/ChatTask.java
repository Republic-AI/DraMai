package com.infinity.ai.login.task.chat;

import com.infinity.ai.login.manager.ChatManager;
import com.infinity.ai.login.manager.IDManager;
import com.infinity.ai.login.task.system.BroadcastMesage;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.consts.RedisKeyEnum;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.chat.ChatData;
import com.infinity.common.msg.chat.ChatRequest;
import com.infinity.common.msg.chat.ChatResponse;
import com.infinity.common.utils.StringUtils;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.RequestIDManager;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;

/**
 * 聊天
 */
@Slf4j
public class ChatTask extends BaseTask<ChatRequest> {

    private static final int MAX_CHAT_HISTORY_SIZE = 20;

    @Override
    public int getCommandID() {
        return ProtocolCommon.kChatCommand;
    }

    @Override
    public boolean run0() {
        ChatRequest msg = this.getMsg();
        String context = msg.getData().getContext();
        if (StringUtils.isEmpty(context)) {
            log.debug("chat context is null,request msg={}", msg);
            sendMessage(buildError(ResultCode.CHAT_FORMAT_ERROR, msg));
            return false;
        }

        if (playerId <= 0) {
            /*log.debug("ChatTask playerId is error,request msg={}", msg);
            sendErrorMsg(ErrorCode.PlayerNotOnlineError, ErrorCode.PlayerNotOnlieErrorMessage, msg);
            return false;*/
            Threads.runAsync(ThreadConst.QUEUE_LOGIC, msg.getPlayerId(), "chat#save", () -> addChatMessage(msg));
            return true;
        }
        GameUser gameUser = GameUserMgr.getGameUser(playerId);
        if (gameUser == null) {
            log.debug("ChatTask playe not error error,request msg={}", msg);
            sendErrorMsg(ErrorCode.PlayerNotOnlineError, ErrorCode.PlayerNotOnlieErrorMessage, msg);
            return true;
        }

        if (!ChatManager.getInstance().canChat(playerId)) {
            log.info("chat limit, playerId = {}, msg={}", playerId, msg);
            return false;
        }

        msg.getData().setSName(gameUser.getNickName());

        //保存数据
        //Threads.runAsync(ThreadConst.QUEUE_LOGIC, msg.getPlayerId(), "chat#save", () -> ChatHelper.addChat(msg));
        Threads.runAsync(ThreadConst.QUEUE_LOGIC, msg.getPlayerId(), "chat#save", () -> addChatMessage(msg));
        //广播数据
        //BroadcastMesage.getInstance().send(playerId, buildMsg(msg).toString());
        return true;
    }

    public void addChatMessage(ChatRequest msg) {
        ChatData chatData = ChatData.builder()
                .barrage(msg.getData().getBarrage())
                .type(msg.getData().getType())
                .content(msg.getData().getContext())
                .sender(msg.getData().getSender())
                .sname(msg.getData().getSName())
                .time(System.currentTimeMillis())
                .msgId(IDManager.getInstance().getChatId())
                .receiver(msg.getData().getReceiver())
                .rName(msg.getData().getRName())
                .replyMsgId(msg.getData().getReplyMsgId())
                .build();

        RedisKeyEnum chatKey = RedisKeyEnum.CHAT;
        RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
        RList<String> chatList = redissonClient.getList(chatKey.getKey(msg.getData().getNpcId()));
        // 添加新的聊天记录到列表的头部
        chatList.add(0, chatData.toString());
        // 如果列表超过了最大聊天记录数，则裁剪列表
        if (chatList.size() > MAX_CHAT_HISTORY_SIZE) {
            chatList.trim(0, MAX_CHAT_HISTORY_SIZE - 1);
        }
        BroadcastMesage.getInstance().send(playerId, buildMsg(chatData, msg).toString());
    }

    private BaseMsg buildMsg(ChatData chatData, ChatRequest msg) {
        ChatResponse response = new ChatResponse();
        response.setRequestId(RequestIDManager.getInstance().RequestID(false));
        response.setPlayerId(playerId);

        ChatRequest.RequestData requestData = msg.getData();
        ChatResponse.ResponseData data = new ChatResponse.ResponseData();
        data.setNpcId(requestData.getNpcId());
        data.setChatData(chatData);
        response.setData(data);
        return response;
    }
}
