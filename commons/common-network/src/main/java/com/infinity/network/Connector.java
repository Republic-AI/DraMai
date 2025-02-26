package com.infinity.network;

import com.infinity.common.ConvertUtil;
import com.infinity.manager.node.*;
import com.infinity.protocol.ServerNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class Connector implements IConnector {
    private int peer_port_;
    private String peer_address_;
    private boolean is_linked_ = false;
    private String node_id_;
    private int channel_id_;

    private Lock connect_event_lock_;
    private List<IConnectorEvent> connector_event_;
    private AsynchronousSocketChannel socket_channel_ = null;
    private ChannelOption channel_option_;

    private INodeManager nodeManager_;
    private HeartbeatChecker checker_;
    private String myNode;
    private Channel channel;

    public String getMyNode() {
        return this.myNode;
    }

    private static class ConnectHandler implements CompletionHandler<Void, Connector> {
        @Override
        public void completed(final Void result, Connector connector) {
            connector.is_linked_ = true;
            connector.notifyConnect();

            Channel channel = new Channel(connector.channel_id_, connector.socket_channel_, connector.channel_option_);
            connector.setChannel(channel);
            ServerNode.server_node server_node = ServerNode.server_node.newBuilder()
                    .setNodeId(connector.node_id_)
                    .setPortLan(connector.peer_port_)
                    .setIpLan(ConvertUtil.ipv4Str2Int(connector.peer_address_))
                    .setChannel(0)
                    .setType(String.valueOf(connector.node_id_.substring(connector.node_id_.lastIndexOf(".") + 1).charAt(0)))
                    .build();


            INode node = new ServiceNode(server_node);
            NodeManager.getInstance().register(node, channel);
            RegisterRequest request = new RegisterRequest(connector.myNode, connector.node_id_);
            channel.write(request.data());

            channel.read();
        }

        @Override
        public void failed(Throwable exc, Connector connector) {
            connector.is_linked_ = false;
            connector.notifyError(exc.getMessage());
        }
    }

    //connectorEvent: 是CenterNode
    public Connector(final String myNode, final String nodeID, IConnectorEvent connectorEvent, INodeManager nodeManager) {
        this.myNode = myNode;
        //setNodeID(nodeID);
        this.node_id_ = nodeID;
        nodeManager_ = nodeManager;

        try {
            channel_option_ = new ChannelOption();
            socket_channel_ = AsynchronousSocketChannel.open();
            socket_channel_.setOption(StandardSocketOptions.TCP_NODELAY, true);
            socket_channel_.setOption(StandardSocketOptions.SO_REUSEADDR, false);
        } catch (IOException e) {
            log.error("failed to create thread pool.msg={}", e.getMessage());
            e.printStackTrace();
        }
        connect_event_lock_ = new ReentrantLock();
        connector_event_ = new LinkedList<>();
        if (connectorEvent != null)
            register(connectorEvent);

        /*checker_ = new HeartbeatChecker(this, node_id_);
        final ICheckerManager checker = nodeManager.getChecker();
        if (checker != null)
            checker.add(checker_);*/
    }

    @Override
    public void register(final IConnectorEvent connectorEvent) {
        if (connectorEvent == null)
            return;

        if (connector_event_.contains(connectorEvent))
            return;

        connect_event_lock_.lock();
        connector_event_.add(connectorEvent);
        connect_event_lock_.unlock();
    }

    @Override
    public void unregister(final IConnectorEvent connectorEvent) {
        if (connectorEvent == null)
            return;

        if (connector_event_.contains(connectorEvent)) {
            connect_event_lock_.lock();
            connector_event_.remove(connectorEvent);
            connect_event_lock_.unlock();
        }
    }

    @Override
    public boolean isClosed() {
        return socket_channel_ != null && !socket_channel_.isOpen() && !isLinked();
    }

    @Override
    public boolean availability() {
        return !isClosed();
    }

    @Override
    public int getPeerPort() {
        return peer_port_;
    }

    @Override
    public String getPeerAddress() {
        return peer_address_;
    }

    @Override
    public int getChannelID() {
        return channel_id_;
    }

    @Override
    public String getNodeID() {
        return node_id_;
    }

    @Override
    public void connect(final String peerAddress, final int peerPort, final String nodeID) {
        peer_address_ = peerAddress;
        peer_port_ = peerPort;
        try {
            ConnectHandler connectHandler = new ConnectHandler();
            channel_id_ = ManagerService.getChannelIDManager().newID();
            SocketAddress socketAddress = new InetSocketAddress(peer_address_, peer_port_);
            socket_channel_.connect(socketAddress, this, connectHandler);
        } catch (Exception ec) {
            log.error("failed build connector.hostName={},hostPort={},eMsg={}",
                    peer_address_, peer_port_, ec.getMessage());
            ec.printStackTrace();
        }
    }

    @Override
    public boolean isLinked() {
        return is_linked_;
    }

    @Override
    public void write(ByteBuffer writeBuffer) {
        if (this.channel.availability()) {
            channel.write(writeBuffer);
        }
    }

    public AsynchronousSocketChannel getSocketHandler() {
        return socket_channel_;
    }

    private void notifyConnect() {
        connect_event_lock_.lock();
        for (IConnectorEvent aEventHandle : connector_event_)
            aEventHandle.OnConnect(this);
        connect_event_lock_.unlock();
    }

    private void notifyDisConnect() {
        connect_event_lock_.lock();
        for (IConnectorEvent aEventHandle : connector_event_)
            aEventHandle.OnDisConnect(this);
        connect_event_lock_.unlock();
    }

    private void notifyError(final String errorMessage) {
        dispose(errorMessage);
    }

    public HeartbeatChecker getChecker() {
        return checker_;
    }

    @Override
    public void dispose(String msg) {
        log.info("Connector dispose, channelId={}", channel_id_);
        connect_event_lock_.lock();
        for (IConnectorEvent aEventHandle : connector_event_)
            aEventHandle.OnError(this, msg);

        this.channel = null;
        ICheckerManager checker = nodeManager_.getChecker();
        if (checker_ != null) {
            checker.remove(checker_);
        }
        connect_event_lock_.unlock();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}