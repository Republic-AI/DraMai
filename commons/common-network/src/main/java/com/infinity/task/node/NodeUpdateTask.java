package com.infinity.task.node;

import com.infinity.manager.node.INode;
import com.infinity.manager.node.NodeManager;
import com.infinity.manager.task.AbstractBaseTask;
import com.infinity.network.IChannel;
import com.infinity.protocol.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.List;

@Slf4j
public class NodeUpdateTask extends AbstractBaseTask {
    private boolean parsed_ = false;
    private boolean is_done_ = false;
    private QueryNodeRequest.query_node_request query_request_ = null;

    @Override
    public int getCommandID() {
//        return CommandDef.kUpdateNodeCommand;
        return ProtocolCommon.kUpdateNodeCommand;
    }

//    @Override
//    public String getThreadMark()
//    {
//        return String.format("NUP: %d", getID());
//    }

    @Override
    public boolean run() {
        if (is_done_)
            return true;

        HeaderOuterClass.Header header = getHeader();
        IChannel channel = getChannel();

        if (!parsed_) {
            if (!parse()) {
                log.error("failed parse register node command node info.header={}", header.toString());
                return true;
            }
            parsed_ = true;
        }

        if (parsed_ && query_request_ != null) {
            ByteBuffer response = makeResponse();
            channel.write(response);
        }

        return is_done_ = true;
    }

    private boolean parse() {
        /*ByteBuffer remainingBuffer = getExtras();
    	int length = remainingBuffer.getInt();
        if(length > remainingBuffer.remaining())
        {
            LoggerHelper.error("failed to parse update node task. the protocol parse error. length=%d,remaining=%d",
                               length,remainingBuffer.remaining());
            return false;
        }

        byte[] regRawData = new byte[length];
        remainingBuffer.get(regRawData);

        try
        {
        	query_request_ = QueryNodeRequest.query_node_request.parseFrom(regRawData);
            if(query_request_ == null)
            {
                LoggerHelper.error("failed to parse node info from node update task.");
                return false;
            }
        }
        catch (InvalidProtocolBufferException e)
        {
            LoggerHelper.error("failed to create task. msg=%s",e.getMessage());
            e.printStackTrace();
            return false;
        }*/

        return true;
    }

    private ByteBuffer makeResponse() {
        if (query_request_ == null)
            return null;


        QueryNodeResponse.query_node_response.Builder builder = QueryNodeResponse.query_node_response.newBuilder();
        builder.setNodeType(String.valueOf(query_request_.getNodeType()));

        List<INode> nodes = NodeManager.getInstance().getNodesWithType(this.getHeader().getSource(), query_request_.getNodeType().charAt(0));
        for (INode node : nodes) {
            ServerNode.server_node serverNode = node.getNodeMeta();
            builder.addNodes(serverNode);
        }
        builder.setRequestId(query_request_.getRequestId());

        QueryNodeResponse.query_node_response response = builder.build();
        return makeCommandResponse(response.toByteArray());
    }
}
