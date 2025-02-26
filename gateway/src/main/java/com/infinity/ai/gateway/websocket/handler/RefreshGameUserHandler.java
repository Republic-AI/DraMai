package com.infinity.ai.gateway.websocket.handler;

import com.infinity.ai.gateway.websocket.GatewayConfig;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.cluster.RefreshPlayerMessage;

public class RefreshGameUserHandler implements IResponseHandler {
    @Override
    public int getCommand() {
        return ProtocolCommon.MSG_CODE_REFRESH_GAMEUSER;
    }

    @Override
    public void preHandler(BaseMsg msg) {

    }

    @Override
    public void hander(BaseMsg msg) {
        RefreshPlayerMessage message = (RefreshPlayerMessage) msg;
        if (!GatewayConfig.getInstance().getNodeId().equals(message.getSourceServiceId())) {
            GameUserMgr.refreshGameUser(message.getUserId());
        }
    }

    @Override
    public void afterHandler(BaseMsg msg) {

    }
}
