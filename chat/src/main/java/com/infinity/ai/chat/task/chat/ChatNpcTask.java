package com.infinity.ai.chat.task.chat;

import com.infinity.ai.chat.manager.ChatManager;
import com.infinity.ai.chat.manager.IDManager;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.chat.ChatData;
import com.infinity.common.msg.chat.ChatNpcRequest;
import com.infinity.common.msg.platform.chat.PChatNpcRequest;
import com.infinity.common.utils.StringUtils;
import com.infinity.common.utils.TrieWordFilter;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import lombok.extern.slf4j.Slf4j;

/**
 * 聊天
 */
@Slf4j
public class ChatNpcTask extends BaseTask<ChatNpcRequest> {

    private static final int MAX_CHAT_HISTORY_SIZE = 20;

    @Override
    public int getCommandID() {
        return ProtocolCommon.NPC_TALK_COMMAND;
    }

    @Override
    public boolean run0() {
        ChatNpcRequest msg = this.getMsg();
        String context = msg.getData().getContext();
        if (StringUtils.isEmpty(context)) {
            log.debug("chat context is null,request msg={}", msg);
            sendMessage(buildError(ResultCode.CHAT_FORMAT_ERROR, msg));
            return false;
        }

        if (TrieWordFilter.getInstance().checkBlockedWords(context)) {
            log.debug("chat context is bad,request msg={}", msg);
            //sendMessage(buildError(ResultCode.CHAT_FORMAT_ERROR, msg));
            return false;
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

        PChatNpcRequest chatNpcRequest = new PChatNpcRequest();
        chatNpcRequest.setPlayerId(playerId);
        PChatNpcRequest.RequestData requestData = new PChatNpcRequest.RequestData();
        requestData.setNpcId(msg.getData().getNpcId());
        requestData.setChatData(addChatMessage(msg));
        chatNpcRequest.setData(requestData);
        Threads.runAsync(ThreadConst.QUEUE_LOGIC, msg.getPlayerId(), "chatToNpc", () -> chatNpc(chatNpcRequest));
        return true;
    }

    public void chatNpc(PChatNpcRequest msg) {
        MessageSender.getInstance().sendMessage(NodeConstant.kPlatformService, msg);
    }

    public ChatData addChatMessage(ChatNpcRequest msg) {
        return ChatData.builder()
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
                .privateMsg(msg.getData().isPrivateMsg())
                .build();
    }
}
