package com.infinity.manager.node;

import com.google.protobuf.ByteString;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.MessageOuterClass;
import com.infinity.protocol.ProtocolCommon;
import com.infinity.protocol.ServerNode;

import java.nio.ByteBuffer;

public class RegisterRequest {
    private final String root_node_id_;
    private String myNode;

    public RegisterRequest(final String myNode, final String nodeID) {
        root_node_id_ = nodeID;
        this.myNode = myNode;
    }

    public ByteBuffer data() {
        HeaderOuterClass.Header.Builder headerBuilder = HeaderOuterClass.Header.newBuilder();
        headerBuilder.setCommand(ProtocolCommon.kRegisterNodeCommand);
        headerBuilder.setSource(myNode);
        headerBuilder.setDestination(root_node_id_);
        headerBuilder.setRtype(ProtocolCommon.kRequestTag);
        HeaderOuterClass.Header header = headerBuilder.build();

        byte[] headerData = header.toByteArray();
        ServerNode.server_node my_node_ = NodeManager.getInstance().getMyNode();
        if (my_node_ == null) {
            ServerNode.server_node.Builder my_node_test = ServerNode.server_node.newBuilder();
            my_node_test.setNodeId(myNode)
                    .setChannel(1);
            my_node_ = my_node_test.build();
        }

        MessageOuterClass.Message build = MessageOuterClass.Message.newBuilder()
                .setContent(ByteString.copyFrom(my_node_.toByteArray()))
                .build();


        byte[] nodeRawData = build.toByteArray();

        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + headerData.length + 4 + nodeRawData.length);
        byteBuffer.putInt(headerData.length);
        byteBuffer.put(headerData);
        byteBuffer.putInt(nodeRawData.length);
        byteBuffer.put(nodeRawData);
        byteBuffer.flip();

        return byteBuffer;
    }
}