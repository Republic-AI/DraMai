package com.infinity.manager.node;

import com.google.protobuf.ByteString;
import com.infinity.manager.task.AbstractBaseTask;
import com.infinity.network.Connector;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.HelloOuterClass;
import com.infinity.protocol.MessageOuterClass;
import com.infinity.protocol.ProtocolCommon;

import java.nio.ByteBuffer;

public class HeartbeatChecker implements IChecker {
    private final long kCheckDuration = 5000L;
    private final Connector connector_;
    private long last_check_timer_ = 0L;
    private final String target_node_id_;

    public HeartbeatChecker(final Connector connector,
                            final String nodeID) {
        target_node_id_ = nodeID;
        connector_ = connector;
        last_check_timer_ = System.currentTimeMillis();
    }

    @Override
    public void update() {
        long nowTime = System.currentTimeMillis();
        if (!connector_.availability()) {
            last_check_timer_ = nowTime;
            return;
        }

        long expireTime = last_check_timer_ + kCheckDuration;
        if (nowTime < expireTime)
            return;

        last_check_timer_ = nowTime;
        ByteBuffer request = makeCommand();
        if (request != null)
            connector_.write(request);
    }

    private ByteBuffer makeCommand() {
        assert target_node_id_ != null;
        HeaderOuterClass.Header.Builder headerBuilder = HeaderOuterClass.Header.newBuilder();
        headerBuilder.setCommand(ProtocolCommon.kHeartBeatCommand);
        headerBuilder.setSource(connector_.getMyNode());
        headerBuilder.setDestination(target_node_id_);
        headerBuilder.setRtype(ProtocolCommon.kRequestTag);
        HeaderOuterClass.Header header = headerBuilder.build();
        byte[] headerData = header.toByteArray();

        HelloOuterClass.Hello.Builder helloBuilder = HelloOuterClass.Hello.newBuilder();
        helloBuilder.setTime(System.currentTimeMillis());

        HelloOuterClass.Hello requestRequest = helloBuilder.build();
        byte[] requestData = requestRequest.toByteArray();

        MessageOuterClass.Message build = MessageOuterClass.Message.newBuilder()
                .setContent(ByteString.copyFrom(requestData))
                .build();
        return AbstractBaseTask.buildPacketBuffer(headerData, build.toByteArray());
    }
}