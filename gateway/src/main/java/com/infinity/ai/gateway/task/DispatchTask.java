package com.infinity.ai.gateway.task;

import com.google.protobuf.ByteString;
import com.infinity.ai.gateway.websocket.GatewayConfig;
import com.infinity.ai.gateway.websocket.GatewayHandler;
import com.infinity.ai.gateway.websocket.handler.IResponseHandler;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.manager.task.AbstractBaseTask;
import com.infinity.network.IChannel;
import com.infinity.network.QueueSelector;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.MessageOuterClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 消息转发
 */
@Slf4j
public class DispatchTask extends AbstractBaseTask {
    private Object attachement;
    private BaseMsg msg;
    private long playerId;

    @Override
    public long getThreadMark() {
        return playerId;
    }

    @Override
    public void init() {
        msg = (BaseMsg) getAttachement();
        this.playerId = msg.getPlayerId() == null ? 0 : msg.getPlayerId();
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.MSG_DISPATCH;
    }

    @Override
    public boolean run() {
        //帧同步
        if (ProtocolCommon.FRAME_SYNC_COMMAND == msg.getCommand()) {
            IResponseHandler handler = GatewayHandler.getInstance().getHandler(0, msg.getCommand());
            handler.hander(msg);
        }

        //根据用户ID负载均衡转发到不同服务：platform/chatgpt/quartz
        IChannel channel = QueueSelector.getInstance().queue(msg);
        if (channel == null) {
            log.error("dispatch fail, channel null,msg={}", msg);
            return true;
        }

        //组装消息
        ByteBuffer byteBuffer = buildPacketBuffer(makeHeader(channel), makeMessage());
        channel.write(byteBuffer);
        return true;
    }


    private byte[] makeHeader(IChannel channel) {
        HeaderOuterClass.Header.Builder headerBuilder = HeaderOuterClass.Header.newBuilder();
        headerBuilder.setCommand(msg.getCommand());
        headerBuilder.setSource(GatewayConfig.getInstance().getNodeId());
        headerBuilder.setDestination(channel.getNodeID());
        headerBuilder.setRtype(com.infinity.protocol.ProtocolCommon.kRequestTag);
        headerBuilder.setCode(0);
        return headerBuilder.build().toByteArray();
    }

    private byte[] makeMessage() {
        return MessageOuterClass.Message.newBuilder()
                .setContent(ByteString.copyFrom(msg.toString().getBytes()))
                .build().toByteArray();
    }


    @Override
    public void setAttachment(Object msg) {
        this.attachement = msg;
    }

    @Override
    public Object getAttachement() {
        return this.attachement;
    }
}
