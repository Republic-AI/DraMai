package com.infinity.ai.gateway.websocket.handler;

import com.infinity.ai.gateway.session.ChannelManageCenter;
import com.infinity.ai.gateway.session.ConnectSession;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.sys.BroadcastRequest;
import com.infinity.manager.node.NodeConstant;
import com.infinity.network.MessageSender;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

//广播消息
public class BroadcastMsgHandler implements IResponseHandler {

    @Override
    public int getCommand() {
        return ProtocolCommon.MSG_BROADCAST;
    }

    @Override
    public void preHandler(BaseMsg msg) {

    }

    @Override
    public void hander(BaseMsg msg) {
        if (ProtocolCommon.FRAME_SYNC_COMMAND == msg.getCommand()) {
            long playerId = msg.getPlayerId();
            String gateway = msg.getGateway();
            String sessionId = msg.getSessionId();

            //发送同步消息给客户端
            msg.clear();
            String data = msg.toString();
            TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(data);
            ChannelGroup allChannels = ChannelManageCenter.getInstance().getAllChannels();
            allChannels.writeAndFlush(textWebSocketFrame);

            msg.setGateway(gateway);
            msg.setSessionId(sessionId);

            //同步给连接到其他网关的客户
            BroadcastRequest response = new BroadcastRequest();
            response.setRequestId(msg.getRequestId());
            response.setSessionId(null);
            response.setPlayerId(playerId);
            response.setData(data);
            MessageSender.getInstance().broadcastMessageToAllService(NodeConstant.kGatewayService, response);
        } else if (msg.getPlayerIds() != null) {
            for (Object obj : msg.getPlayerIds()) {
                Long playerId = (long)obj;
                Channel channel = null;
                ConnectSession session = ChannelManageCenter.getInstance().getSession(playerId);
                if (session != null)
                    channel = session.getChannel();
                if (channel != null) {
                    String data = msg.toString();
                    TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(data);
                    channel.writeAndFlush(textWebSocketFrame);
                }
            }
        } else {
            Object data = msg.getData();
            if (data != null) {
                TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame((String) data);
                ChannelGroup allChannels = ChannelManageCenter.getInstance().getAllChannels();
                allChannels.writeAndFlush(textWebSocketFrame);
            }
        }
    }

    @Override
    public void afterHandler(BaseMsg msg) {

    }
}
