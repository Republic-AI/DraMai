package com.infinity.network;

import com.google.protobuf.ByteString;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.utils.StringUtils;
import com.infinity.manager.node.INode;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.node.NodeManager;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.MessageOuterClass;
import com.infinity.register.NodeConfig;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.infinity.manager.task.AbstractBaseTask.buildPacketBuffer;

@Slf4j
public class MessageSender {
    private NodeConfig myNode;

    private static class Holder {
        private static final MessageSender kInstance = new MessageSender();
    }

    public static MessageSender getInstance() {
        return Holder.kInstance;
    }

    //发送到网关服务器
    public void sendMessageToGateWay(String userGateWayId, BaseMsg msg) {
        this.sendMessageToGateWay(userGateWayId, myNode.getNodeId(), msg);
    }

    //发送到网关服务器
    public void sendMessageToGateWay(String userGateWayId, String sourceId, BaseMsg msg) {
        IChannel channel = getGateWayChannel(userGateWayId, msg);
        if (channel == null) {
            log.error("send message fail, channel is null,from={},to={},msg={}", sourceId, userGateWayId, msg);
            return;
        }

        byte[] headerData = makeHeader(ProtocolCommon.MSG_RESPONSE, msg.getCommand(), msg.getCode(), sourceId, channel.getNodeID(), true);
        byte[] data = makeMessage(msg);
        sendMessage(channel, headerData, data);
    }

    //发送到指定服务器
    public void sendMessage(String nodeId, BaseMsg msg) {
        sendMessage(nodeId, myNode.getNodeId(), msg);
    }

    //发送到指定服务器
    public void sendMessage(String nodeId, String sourceId, BaseMsg msg) {
        char type = nodeId.charAt(nodeId.lastIndexOf(".") + 1);
        IChannel channel = QueueSelector.getInstance().findUserService(nodeId, msg.getPlayerId(), type);
        if (channel == null) {
            log.error("send message fail, channel is null,from={},type={},msg={}", sourceId, type, msg);
            return;
        }

        int command = type == NodeConstant.kGatewayService ? ProtocolCommon.MSG_RESPONSE : msg.getCommand();
        byte[] headerData = makeHeader(command, msg.getCommand(), msg.getCode(), sourceId, nodeId, true);
        byte[] data = makeMessage(msg);
        sendMessage(channel, headerData, data);
    }

    //随机发送
    public void sendMessage(char type, BaseMsg msg) {
        sendMessage(type, myNode.getNodeId(), msg);
    }

    //随机发送
    public void sendMessage(char type, String sourceId, BaseMsg msg) {
        IChannel channel = QueueSelector.getInstance().loadBalance(msg.getPlayerId(), type);
        if (channel == null) {
            log.error("send message fail, channel is null,from={},type={},msg={}", sourceId, type, msg);
            return;
        }

        int command = type == NodeConstant.kGatewayService ? ProtocolCommon.MSG_RESPONSE : msg.getCommand();
        byte[] headerData = makeHeader(command, msg.getCommand(), msg.getCode(), sourceId, channel.getNodeID(), true);
        byte[] data = makeMessage(msg);
        sendMessage(channel, headerData, data);
    }

    public void sendMessage(IChannel channel, final byte[] headerData, final byte[] responseData) {
        if (channel != null) {
            ByteBuffer byteBuffer = buildPacketBuffer(headerData, responseData);
            channel.write(byteBuffer);
        }
    }

    public void broadcastMessageToAllService(BaseMsg msg) {
        Map<String, INode> allServices = NodeManager.getInstance().getAllNodes();
        if (allServices == null || allServices.size() == 0) {
            log.warn("broadcastMessageToAllService fail, service list is null......");
            return;
        }

        byte[] data = makeMessage(msg);
        Iterator<Map.Entry<String, INode>> entries = allServices.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, INode> entry = entries.next();
            INode node = entry.getValue();
            //过滤python服务
            if(node.getType() == NodeConstant.kPythonService){
                continue;
            }

            int command = (node.getType() == NodeConstant.kGatewayService) ? ProtocolCommon.MSG_RESPONSE : msg.getCommand();
            byte[] header = makeHeader(command, msg.getCommand(), msg.getCode(), myNode.getNodeId(), node.getNodeID(), true);

            try {
                sendMessage(node.getChannel(), header, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessageToAllService(char type, BaseMsg msg) {
        List<INode> allServices = NodeManager.getInstance().getNodesWithType(type);
        if (allServices == null || allServices.size() == 0) {
            //log.debug("broadcastMessageToAllService fail, service list is null......");
            return;
        }

        byte[] data = makeMessage(msg);
        for (INode node : allServices) {
            int command = (node.getType() == NodeConstant.kGatewayService) ? ProtocolCommon.MSG_RESPONSE : msg.getCommand();
            byte[] header = makeHeader(command, msg.getCommand(), msg.getCode(), myNode.getNodeId(), node.getNodeID(), true);

            try {
                sendMessage(node.getChannel(), header, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] makeHeader(int command, int cmd, int errorCode, String sourceId, String destId, boolean request) {
        HeaderOuterClass.Header.Builder headerBuilder = HeaderOuterClass.Header.newBuilder();
        headerBuilder.setCommand(command);
        headerBuilder.setCmd(cmd);
        headerBuilder.setSource(sourceId);
        headerBuilder.setDestination(destId);
        headerBuilder.setRtype(request ? com.infinity.protocol.ProtocolCommon.kRequestTag : com.infinity.protocol.ProtocolCommon.kResponseTag);
        headerBuilder.setCode(errorCode);
        return headerBuilder.build().toByteArray();
    }

    public byte[] makeMessage(BaseMsg msg) {
        return MessageOuterClass.Message.newBuilder()
                .setContent(ByteString.copyFrom(msg.toString().getBytes()))
                .build()
                .toByteArray();
    }


    public IChannel getGateWayChannel(String userGateWayId, BaseMsg request) {
        INode node;
        if (StringUtils.isNotEmpty(request.getGateway())) {
            node = NodeManager.getInstance().getNode(request.getGateway());
            if (node != null) {
                return node.getChannel();
            }
        }

        if (StringUtils.isNotEmpty(userGateWayId)) {
            node = NodeManager.getInstance().getNode(userGateWayId);
            if (node != null) {
                return node.getChannel();
            }
        }

        return null;
    }

    public void setMyNode(NodeConfig myNode) {
        this.myNode = myNode;
    }
}
