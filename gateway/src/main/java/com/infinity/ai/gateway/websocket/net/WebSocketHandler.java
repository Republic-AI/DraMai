package com.infinity.ai.gateway.websocket.net;

import com.infinity.ai.gateway.session.ChannelManageCenter;
import com.infinity.ai.gateway.session.ConnectSession;
import com.infinity.ai.gateway.websocket.GatewayConfig;
import com.infinity.ai.gateway.websocket.MessageHelper;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtoHelper;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.player.LogoutRequest;
import com.infinity.network.MessageSender;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.ContinuationWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private static Logger log = LoggerFactory.getLogger(WebSocketHandler.class);
    private StringBuilder messageBuffer = new StringBuilder();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            // 将片段添加到消息缓冲区
            messageBuffer.append(textFrame.text());
        } else if (frame instanceof ContinuationWebSocketFrame) {
            ContinuationWebSocketFrame continuationFrame = (ContinuationWebSocketFrame) frame;
            // 将片段添加到消息缓冲区
            messageBuffer.append(continuationFrame.text());
        } else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
            ByteBuf byteBuf = binaryFrame.content();
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(bytes);
            String msg = new String(bytes, StandardCharsets.UTF_8);
            // 将片段添加到消息缓冲区
            messageBuffer.append(msg);
        }

        // 如果是最后一个片段，处理完整的消息
        if (frame.isFinalFragment()) {
            String completeMessage = messageBuffer.toString();
            //清空缓冲区
            messageBuffer.setLength(0);
            handleCompleteMessage(ctx, completeMessage);
        }
    }

    private void handleCompleteMessage(ChannelHandlerContext ctx, String msg) {
        String clientIP = ctx.channel().attr(IPAddressHandler.CLIENT_IP).get();
        log.debug(">>>>>服务器收到:clientIp={},msg={}", clientIP, msg);
        BaseMsg baseMsg = ProtoHelper.parseJSON(msg);
        if (ProtocolCommon.SYS_HEART_COMMAND == baseMsg.getCommand()) {
            return;
        }

        ConnectSession session = ChannelManageCenter.getInstance().getSession(ctx.channel());
        if (session != null) {
            baseMsg.setSessionId(session.getSesseionId());
            baseMsg.setPlayerId(session.getUserId());
            baseMsg.setGateway(GatewayConfig.getInstance().getNodeId());
            baseMsg.setIp(clientIP);
        } else {
            baseMsg.setGateway(GatewayConfig.getInstance().getNodeId());
            baseMsg.setSessionId(ChannelManageCenter.getInstance().getTempID(ctx.channel()));
            baseMsg.setIp(clientIP);
        }
        MessageHelper.dispatchMsg(baseMsg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug(">>>>>new socket connect, channelActive,IP={}", ctx.channel().attr(IPAddressHandler.CLIENT_IP).get());
        ChannelManageCenter.getInstance().addChannel(ctx.channel());
        log.debug("new client enter, client connect number={},ip={}",
                ChannelManageCenter.getInstance().getConnectNum(),
                ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(">>>>>exceptionCaught", cause);
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        try {
            log.info(">>>>>socket connect Disconnect, handlerRemoved");
            ChannelManageCenter channelManageCenter = ChannelManageCenter.getInstance();
            channelManageCenter.removeTempSession(ctx.channel());
            ConnectSession session = channelManageCenter.getSession(ctx.channel());
            if (session != null) {
                channelManageCenter.removeSessionPool(session.getUserId());
                channelManageCenter.clearSession(session.getUserId());
                channelManageCenter.removeChannel(ctx.channel());

                GameUserMgr.removeGameUser(session.getUserId(), () -> {
                    LogoutRequest logoutRequest = new LogoutRequest();
                    logoutRequest.setPlayerId(session.getUserId());
                    LogoutRequest.RequestData requestData = new LogoutRequest.RequestData();
                    requestData.setSourceServiceId(GatewayConfig.getInstance().getNodeId());
                    requestData.setType(0);
                    MessageSender.getInstance().broadcastMessageToAllService(logoutRequest);
                });
            }
            log.debug("client disconnect, client connect number={}", ChannelManageCenter.getInstance().getConnectNum());
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            Channel channel = ctx.channel();
            channel.disconnect();
            channel.close();
        }
        super.handlerRemoved(ctx);
    }

}
