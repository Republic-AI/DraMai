package com.infinity.task.node;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.infinity.manager.task.AbstractBaseTask;
import com.infinity.network.IChannel;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.HelloOuterClass;
import com.infinity.protocol.MessageOuterClass;
import com.infinity.protocol.ProtocolCommon;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 心跳
 */
@Slf4j
public class HeartbeatTask extends AbstractBaseTask {
    private boolean parsed_ = false;
    private boolean is_done_ = false;
    private HelloOuterClass.Hello hello_request_;

    @Override
    public int getCommandID() {
        return ProtocolCommon.kHeartBeatCommand;
    }

    @Override
    public boolean run() {
        if (is_done_)
            return true;

        IChannel channel = getChannel();
        HeaderOuterClass.Header header = getHeader();

        if (!parsed_) {
            if (header != null && header.getRtype().charAt(0) == ProtocolCommon.kResponseTag.charAt(0)) {
                // 处理响应
                parsed_ = true;
                is_done_ = true;
                //log.debug("收到心跳信息，type={},from={},response={}", header.getRtype(), header.getSource(), System.currentTimeMillis());
                // 如果是请求回应
//                ResponseOk.response_ok response = parseResponse();
//                assert response != null;
//                LoggerHelper.info("register response result: %d", response.getCode());
                return true;
            } else if (!parse()) {
                assert header != null;
                log.error("failed parse register node command node info.header={}",
                        header.toString());
                return true;
            }
            parsed_ = true;
        }

        if (parsed_ && hello_request_ != null) {
            // post response
            channel.write(makeResponse());
        }
        //log.debug("收到心跳信息，type={},from={},response={}", header.getRtype(), header.getSource(), hello_request_.getTime());
        return is_done_ = true;
    }

    private ByteBuffer makeResponse() {
        HelloOuterClass.Hello.Builder res = HelloOuterClass.Hello.newBuilder();
        res.setTime(System.currentTimeMillis());

        MessageOuterClass.Message.Builder builder = MessageOuterClass.Message.newBuilder()
                .setContent(ByteString.copyFrom(res.build().toByteArray()));

        return makeCommandResponse(builder.build().toByteArray());
    }

    private boolean parse() {
        try {
            hello_request_ = HelloOuterClass.Hello.parseFrom(getExtras());
        } catch (InvalidProtocolBufferException e) {
            log.error("failed to create task. msg={}", e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
