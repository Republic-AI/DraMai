package com.infinity.ai.gateway.websocket.handler;

import com.infinity.ai.gateway.session.ChannelManageCenter;
import com.infinity.ai.gateway.websocket.MessageHelper;
import com.infinity.ai.gateway.websocket.GatewayConfig;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.cluster.RefreshPlayerMessage;
import com.infinity.network.MessageSender;

public class LoginMsgHandler implements IResponseHandler {
    @Override
    public int getCommand() {
        return ProtocolCommon.LOGIN_COMMAND;
    }

    @Override
    public void preHandler(BaseMsg msg) {

    }

    @Override
    public void hander(BaseMsg msg) {
        int result = msg.getCode();

        // 登录之后的消息是多播的，需要鉴别
        long userId = msg.getPlayerId();
        GameUser gameUser = GameUserMgr.getGameUser(userId);

        if (result == 0) {
            boolean bind = ChannelManageCenter.getInstance().bind(msg.getSessionId(), userId);
            if (bind) {
                gameUser.setGatewayServiceId(GatewayConfig.getInstance().getNodeId());
                gameUser.setSessionId(msg.getSessionId());
                GameUserMgr.addGameUser(gameUser, () -> {
                    // 发送刷新通知
                    RefreshPlayerMessage refreshPlayerMessage = new RefreshPlayerMessage();
                    refreshPlayerMessage.setUserId(gameUser.getUserId());
                    refreshPlayerMessage.setSourceServiceId(GatewayConfig.getInstance().getNodeId());
                    MessageSender.getInstance().broadcastMessageToAllService(refreshPlayerMessage);
                });
            }
        }

        MessageHelper.outMsgToClient(msg);
    }

    @Override
    public void afterHandler(BaseMsg msg) {

    }
}
