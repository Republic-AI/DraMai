package com.infinity.manager.node;

import com.infinity.common.base.thread.Threads;
import com.infinity.common.base.thread.timer.IntervalTimer;
import com.infinity.manager.task.AbstractBaseTask;
import com.infinity.network.*;
import com.infinity.protocol.HeaderOuterClass;
import com.infinity.protocol.ProtocolCommon;
import com.infinity.protocol.QueryNodeRequest;
import com.infinity.protocol.ServerNode;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class CenterNode implements IConnectorEvent {
    private final String node_id_;
    private final String peer_ip_;
    private final int peer_port_;
    private final int max_channel_size_;

    private boolean exit_ = false;
    private boolean is_registered_ = false;


    private final List<IConnector> connectors_;
    private final Long connectors_lock_ = 1L;

    private CenterLooper root_looper_;
    private INodeManager node_manager_;
    private int next_connector_ = 0;
    private String myNode;
    private int failNum = 0;

    public String getMyNode() {
        return this.myNode;
    }

    public CenterNode(final String myNode, final String nodeID,
                      final String peerAddress,
                      final int peerPort,
                      final int maxChannel,
                      final INodeManager nodeManager) {
        this.myNode = myNode;
        node_id_ = nodeID;
        peer_ip_ = peerAddress;
        peer_port_ = peerPort;
        max_channel_size_ = maxChannel;
        connectors_ = new LinkedList<>();
        node_manager_ = nodeManager;
    }

    public void dispose() {
        node_manager_ = null;
        exit_ = true;

    }

    public void start() {
        if (root_looper_ == null)
            root_looper_ = new CenterLooper(this);
        Threads.addListener(NetThreadConst.TIMER_positive_connect_live, "positive_connect_live", new IntervalTimer(1, 5000) {
            @Override
            public boolean exec0(int interval) {
                return root_looper_.run();
            }
        });

    }

    public void query(final char queryType) {
        final byte[] queryHeader = makeQueryHeader();
        final byte[] queryContent = makeQueryContent(queryType);

        final ByteBuffer sendContent = AbstractBaseTask.buildPacketBuffer(queryHeader, queryContent);
        IConnector connector = this.getConnector();
        if (connector != null && this.isRegistered()) {
            connector.write(sendContent);
        }
    }

    private byte[] makeQueryHeader() {
        var headerBuilder = HeaderOuterClass.Header.newBuilder();
        headerBuilder.setRtype(ProtocolCommon.kRequestTag);
        headerBuilder.setDestination(this.getNodeID());
        headerBuilder.setSource(myNode);
        headerBuilder.setCode(0);
        headerBuilder.setCommand(ProtocolCommon.kUpdateNodeCommand);
        return headerBuilder.build().toByteArray();
    }

    private byte[] makeQueryContent(final char queryType) {
        var requestBuilder = QueryNodeRequest.query_node_request.newBuilder();
        requestBuilder.setRequestId(RequestIDManager.getInstance().RequestID(false));
        requestBuilder.setNodeType(String.format("%c", queryType));
        return requestBuilder.build().toByteArray();
    }

    public void setRegisteredDone() {
        is_registered_ = true;
    }

    public boolean isRegistered() {
        return is_registered_ && connectors_.size() > 0;
    }

    public IConnector getConnector() {
        var index = (next_connector_++) % connectors_.size();
        return connectors_.get(index);
    }

    private String getNodeID() {
        return node_id_;
    }

    private String getPeerIp() {
        return peer_ip_;
    }

    private int getPeerPort() {
        return peer_port_;
    }

    private int getMaxChannelSize() {
        return max_channel_size_;
    }

    private void addConnector(IConnector connector) {
        synchronized (connectors_lock_) {
            connectors_.add(connector);
        }
    }

    private void removeConnector(IConnector connector) {
        synchronized (connectors_lock_) {
            connectors_.remove(connector);
        }

        if (connectors_.size() <= 0)
            is_registered_ = false;
    }

    /**
     * 如果到目标节点链接变少了，那么需要新的链接
     */
    private final class CenterLooper {
        private IConnectorEvent event_handler_;

        CenterLooper(IConnectorEvent eventHandle) {
            event_handler_ = eventHandle;
        }

        public boolean run() {
            if (exit_) return true;

            if (connectors_.size() < getMaxChannelSize()) {
                //检验是否已经有连接进来
                INode node = node_manager_.getNode(node_id_);
                if (node != null) {
                    ServerNode.server_node nodeInfo = node.getNodeMeta();
                    if (nodeInfo != null && nodeInfo.getChannel() == 1) {
                        log.info("connection has already been established with it，nodeId={}", node_id_);
                        ((NodeManager) node_manager_).removePositive(node_id_);
                        dispose();
                        return true;
                    }
                }

                IConnector newConnector = new Connector(myNode, node_id_, event_handler_, node_manager_);
                newConnector.connect(peer_ip_, peer_port_, node_id_);
            }
            return false;
        }
    }

    public void clearConnector(int channelId) {
        IConnector connector_ = null;
        for (IConnector connector : connectors_) {
            if (connector.getChannelID() == channelId) {
                connector_ = connector;
                break;
            }
        }

        if (connector_ != null) {
            connector_.dispose("Channel close");
            removeConnector(connector_);
        }
    }

    @Override
    public void OnConnect(IConnector connector) {
        addConnector(connector);
    }

    @Override
    public void OnDisConnect(IConnector connector) {
        removeConnector(connector);
    }

    @Override
    public void OnError(IConnector connector, String errorMessage) {
        failNum += 1;
        if (failNum >= max_channel_size_ * 5) {
            log.error("Connect custer[{}] fail, out limit num {}, connect num={}",
                    this.node_id_, max_channel_size_ * 5, failNum);

            this.exit_ = true;
            ICheckerManager checker = node_manager_.getChecker();
            IChecker heartCheker = connector.getChecker();
            if (heartCheker != null) {
                checker.remove(heartCheker);
            }

            ((NodeManager) node_manager_).removePositive(this.node_id_);

        }

        log.error("connect to node[{}] fail, err: {}[{}]", this.node_id_, errorMessage, failNum);
    }
}