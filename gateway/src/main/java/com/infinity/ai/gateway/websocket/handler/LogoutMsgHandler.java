package com.infinity.ai.gateway.websocket.handler;

import com.infinity.ai.gateway.session.ChannelManageCenter;
import com.infinity.ai.gateway.session.ConnectSession;
import com.infinity.ai.gateway.websocket.GatewayConfig;
import com.infinity.ai.gateway.websocket.MessageHelper;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.cluster.RefreshPlayerMessage;
import com.infinity.network.MessageSender;
import io.netty.channel.Channel;

public class LogoutMsgHandler implements IResponseHandler {
    @Override
    public int getCommand() {
        return ProtocolCommon.OFF_LINE_COMMAND;
    }

    @Override
    public void preHandler(BaseMsg msg) {

    }

    @Override
    public void hander(BaseMsg msg) {
        int result = msg.getCode();
        //发送响应到客户端
        MessageHelper.outMsgToClient(msg);

        if (result == 0) {
            // 发送刷新通知
            GameUserMgr.removeGameUser(msg.getPlayerId(), null);

            RefreshPlayerMessage refreshPlayerMessage = new RefreshPlayerMessage();
            refreshPlayerMessage.setUserId(msg.getPlayerId());
            refreshPlayerMessage.setSourceServiceId(GatewayConfig.getInstance().getNodeId());
            refreshPlayerMessage.setPlayerId(msg.getPlayerId());
            refreshPlayerMessage.setOperate(1);

            MessageSender.getInstance().broadcastMessageToAllService(refreshPlayerMessage);
            //关闭连接
            ConnectSession session = ChannelManageCenter.getInstance().getSession(msg.getPlayerId());
            if(session != null) {
                Channel channel = session.getChannel();
                channel.close();
            }
        }
    }

    @Override
    public void afterHandler(BaseMsg msg) {

    }
}
