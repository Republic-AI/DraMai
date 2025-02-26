package com.infinity.ai.gateway.task;

import com.infinity.ai.gateway.websocket.GatewayHandler;
import com.infinity.ai.gateway.websocket.handler.IResponseHandler;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtoHelper;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.common.ResponseOk;
import com.infinity.manager.task.AbstractBaseTask;
import com.infinity.protocol.HeaderOuterClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息返回给客户端
 */
@Slf4j
public class ResponseTask extends AbstractBaseTask {
    private BaseMsg msg;
    private long playerId;

    public ResponseTask() {
    }

    @Override
    public long getThreadMark() {
        return playerId;
    }

    @Override
    public void init() {
        HeaderOuterClass.Header header = this.getHeader();
        int errCode = header.getCode();
        String message = new String(this.getExtras());
        log.debug("收到响应信息={}", message);
        msg = ProtoHelper.parseResponseJSON(message, errCode != 0 ? ResponseOk.getCmd() : header.getCmd());
        this.playerId = msg.getPlayerId() == null ? 0 : msg.getPlayerId();
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.MSG_RESPONSE;
    }

    @Override
    public boolean run() {
        IResponseHandler handler = GatewayHandler.getInstance().getHandler(this.getHeader().getCode(), msg.getCommand());
        handler.hander(msg);
        return true;
    }

}
