package com.infinity.task.node;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.infinity.manager.node.CenterNode;
import com.infinity.manager.node.INode;
import com.infinity.manager.node.NodeManager;
import com.infinity.manager.node.ServiceNode;
import com.infinity.manager.task.AbstractBaseTask;
import com.infinity.network.IChannel;
import com.infinity.protocol.HeaderOuterClass.Header;
import com.infinity.protocol.MessageOuterClass;
import com.infinity.protocol.ProtocolCommon;
import com.infinity.protocol.ResponseOk.response_ok;
import com.infinity.protocol.ServerNode;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 节点注册事件
 */
@Slf4j
public class NodeRegisterTask extends AbstractBaseTask {
    private boolean parsed_ = false;
    private ServerNode.server_node node_info_ = null;
    private boolean is_done_ = false;

    @Override
    public long getThreadMark() {
        return 0L;
    }

    @Override
    public final int getCommandID() {
        return ProtocolCommon.kRegisterNodeCommand;
    }

    @Override
    public boolean run() {
        if (is_done_)
            return true;

        IChannel channel = getChannel();
        Header header = getHeader();

        if (!parsed_) {
            if (header != null && header.getRtype().charAt(0) == ProtocolCommon.kResponseTag.charAt(0)) {
                parsed_ = true;
                is_done_ = true;

                // 如果是请求回应
                response_ok response = parseResponse();
                assert response != null;
                if (response.getCode() != 0) {
                    log.error("failed to register to {}. errorCode={}, errorMsg={}",
                            header.getDestination(), response.getCode(), response.getMessage());
                    return false;
                }

                CenterNode centerNode = NodeManager.getInstance().getPositiveNode(this.getHeader().getSource());
                if (centerNode != null)
                    centerNode.setRegisteredDone();

                return true;
            } else if (!parse()) {
                assert header != null;
                log.error("failed parse register node command node info.header={}",
                        header.toString());
                return true;
            }
            parsed_ = true;
        }

        if (node_info_ != null) {
            assert header != null;
            log.info("register service node. chanId={}, header={}, nameName={}",
                    channel.getID(), header.getCommand(), node_info_.getNodeId());

            INode node = new ServiceNode(node_info_);
            NodeManager.getInstance().register(node, channel);

            // post response
            channel.write(makeResponse());
        }

        return is_done_ = true;
    }

    private ByteBuffer makeResponse() {
        response_ok.Builder builder = response_ok.newBuilder();
        builder.setCode(ProtocolCommon.kResponseOK);
        response_ok response = builder.build();


        MessageOuterClass.Message build = MessageOuterClass.Message.newBuilder()
                .setContent(ByteString.copyFrom(response.toByteArray())).build();

        return makeCommandResponse(build.toByteArray());
    }

    private response_ok parseResponse() {
        response_ok response;
        /*ByteBuffer remainingBuffer = getExtras();
        int length = remainingBuffer.getInt();
        if (length > remainingBuffer.remaining())
        {
            LoggerHelper.error("failed to parse register node task. the protocol parse error. length=%d,remaining=%d",
                               length,remainingBuffer.remaining());
            return null;
        }

        byte[] regRawData = new byte[length];
        remainingBuffer.get(regRawData);*/

        try {
            response = response_ok.parseFrom(ByteString.copyFrom(getExtras()).toByteArray());
            if (response == null) {
                log.error("failed to parse node info from node register task.");
                return null;
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("failed to create task. msg={}", e.getMessage());
            e.printStackTrace();
            return null;
        }

        return response;
    }

    private boolean parse() {
        /*ByteBuffer remainingBuffer = getExtras();
        int length = remainingBuffer.getInt();
        if(length > remainingBuffer.remaining())
        {
            LoggerHelper.error("failed to parse register node task. the protocol parse error. length=%d,remaining=%d",
                               length,remainingBuffer.remaining());
            return false;
        }

        byte[] regRawData = new byte[length];
        remainingBuffer.get(regRawData);*/

        try {

            node_info_ = ServerNode.server_node.parseFrom(getExtras());

            //node_info_ = ServerNode.server_node.parseFrom(regRawData);
            if (node_info_ == null) {
                log.error("failed to parse node info from node register task.");
                return false;
            }
        } catch (InvalidProtocolBufferException e) {
            log.error("failed to create task. msg={}", e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
