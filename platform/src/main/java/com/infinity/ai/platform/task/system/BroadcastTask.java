package com.infinity.ai.platform.task.system;

import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.sys.BroadcastRequest;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.MessageSender;
import com.infinity.network.RequestIDManager;
import com.infinity.task.IRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class BroadcastTask extends BaseTask implements IRequest {
    private long playerId;
    private String message;
    private long requestId;
    private List<Long> playerIds;

    public BroadcastTask(final long playerId, String message) {
        this.playerId = playerId;
        this.message = message;
    }

    public BroadcastTask(final long playerId, final List<Long> playerIds, String message) {
        this.playerId = playerId;
        this.message = message;
        this.playerIds = playerIds;
    }

    @Override
    public long getThreadMark() {
        return playerId;
    }

    @Override
    public long getRequestID() {
        return requestId;
    }

    @Override
    public void init() {
        this.requestId = RequestIDManager.getInstance().RequestID(false);
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.MSG_BROADCAST;
    }

    @Override
    public boolean run0() {
        if (message == null || message.trim().length() == 0) {
            log.debug("BroadcastTask break, message is null");
            return true;
        }

        BroadcastRequest response = new BroadcastRequest();
        response.setRequestId(requestId);
        response.setSessionId(null);
        response.setPlayerId(playerId);
        response.setData(message);
        response.setPlayerIds(playerIds);
        MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kGatewayService, response);
        return true;
    }

}