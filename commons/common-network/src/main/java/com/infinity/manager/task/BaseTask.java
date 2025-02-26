package com.infinity.manager.task;

import com.google.protobuf.ByteString;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.base.exception.BusinessException;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtoHelper;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.common.ResponseOk;
import com.infinity.manager.node.NodeConstant;
import com.infinity.network.IChannel;
import com.infinity.network.MessageSender;
import com.infinity.network.QueueSelector;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.MessageOuterClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public abstract class BaseTask<T extends BaseMsg> extends AbstractBaseTask {
    protected boolean parse = false;
    protected BaseMsg msg;
    protected long playerId;

    @Override
    public void init() {
        super.init();
        parse();
        playerId = this.msg.getPlayerId();
    }

    @Override
    public boolean run() {
        try {
            return run0();
        } catch (BusinessException e) {
            sendErrorMsg(e.getCode(), e.getMessage(), msg);
            e.printStackTrace();
            return true;
        } catch (Exception e) {
            sendErrorMsg(ErrorCode.SystemError, e.getMessage(), msg);
            e.printStackTrace();
            return true;
        }
    }

    protected abstract boolean run0();

    private void parse() {
        try {
            msg = (T) ProtoHelper.parseJSON(new String(getExtras()), getCommandID());
            parse = true;
        } catch (BusinessException e) {
            parse = false;
            sendErrorMsg(e.getCode(), e.getMessage(), msg);
            e.printStackTrace();
        } catch (Exception e) {
            parse = false;
            sendErrorMsg(ErrorCode.SystemError, e.getMessage(), msg);
            e.printStackTrace();
        }

    }

    public T getMsg() {
        return (T) msg;
    }

    //生成错误信息返回
    protected BaseMsg buildError(final int errorCode, final String errorMessage, BaseMsg msg) {
        ResponseOk responseOk = ResponseOk.build(msg);
        responseOk.setRequestId(msg.getRequestId());
        responseOk.setCommand(this.msg.getCommand());
        responseOk.setType(this.msg.getType());
        responseOk.setCode(errorCode);
        responseOk.setMessage(errorMessage);
        responseOk.setPlayerId(playerId);
        responseOk.setSessionId(msg.getSessionId());
        return responseOk;
    }

    //生成错误信息返回
    protected BaseMsg buildError(final ResultCode result, BaseMsg msg) {
        return buildError(result.code, result.message, msg);
    }

    protected void broadcastMessageToService(char nodeType, BaseMsg message) {
        MessageSender.getInstance().broadcastMessageToAllService(nodeType, message);
    }

    protected void sendMessage(String nodeId, BaseMsg msg) {
        MessageSender.getInstance().sendMessage(nodeId, msg);
    }

    protected void sendMessage(BaseMsg msg) {
        IChannel channel = getChannel();
        if (channel != null && msg != null) {
            sendMessage(channel, msg);
        }
    }

    protected void sendErrorMsg(final int errorCode, final String errorMessage, BaseMsg msg) {
        sendMessage(buildError(errorCode, errorMessage, msg));
    }

    protected void sendMessage(IChannel channel, BaseMsg msg) {
        if (channel != null && msg != null) {
            HeaderOuterClass.Header header = makeHeader(ProtocolCommon.MSG_RESPONSE, msg.getCode());
            ByteBuffer byteBuffer = buildPacketBuffer(header.toByteArray(), makeMessage(msg));
            channel.write(byteBuffer);
        }
    }

    protected void sendMessageToService(IChannel channel, BaseMsg msg) {
        if (channel != null && msg != null) {
            HeaderOuterClass.Header header = makeHeader(msg.getCommand(), msg.getCode());
            ByteBuffer byteBuffer = buildPacketBuffer(header.toByteArray(), makeMessage(msg));
            channel.write(byteBuffer);
        }
    }

    //给玩家发消息
    public void sendMessage(long userId, BaseMsg msg) {
        GameUser gameUser = GameUserMgr.getGameUser(userId);
        log.debug("send msg -> {},{}", gameUser, msg);
        if (gameUser != null) {
            sendMessage(gameUser.getGatewayServiceId(), msg);
        }
    }

    private byte[] makeMessage(BaseMsg message) {
        return MessageOuterClass.Message.newBuilder()
                .setContent(ByteString.copyFrom(message.toString().getBytes()))
                .build().toByteArray();
    }

    public void sendToChatMessage(BaseMsg msg) {
        IChannel channel = QueueSelector.getInstance().loadBalance(msg.getPlayerId(), NodeConstant.kChatService);
        if (channel == null) {
            log.error("send message to chatgpt services fail, node is null; msg={}", msg.toString());
            throw new RuntimeException("send message to chatgpt services fail, node is null");
        }

        sendMessageToService(channel, msg);
    }
}
