package com.infinity.manager.node;

import com.alibaba.fastjson.JSON;
import com.infinity.common.base.thread.NameParam;
import com.infinity.common.base.thread.Threads;
import com.infinity.network.IChannel;
import com.infinity.network.ManagerService;
import com.infinity.network.NetThreadConst;
import com.infinity.protocol.ServerNode;
import com.infinity.register.NodeConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public final class NodeManager implements INodeManager {
    private final Map<String, INode> passive_nodes_;
    private Map<String, CenterNode> positive_nodes = new HashMap<>();
    private ServerNode.server_node my_node_info_;
    private LoopChecker checker_;
    private static NodeManager kInstance_ = null;

    public static NodeManager getInstance() {
        if (kInstance_ == null)
            kInstance_ = new NodeManager();
        return kInstance_;
    }

    public static String getMyNodeID() {
        return NodeManager.getInstance().getMyNode().getNodeId();
    }

    private NodeManager() {
        passive_nodes_ = new ConcurrentHashMap<>();
        checker_ = new LoopChecker();
        Threads.regist(new NameParam(NetThreadConst.TIMER_positive_connect_live, 1, 1000));
    }

    @Override
    public void init() {
        passive_nodes_.clear();
        checker_.start();
    }

    @Override
    public void fini() {
        checker_.end();
        for (INode node : passive_nodes_.values()) {
            if (node == null)
                continue;
            node.dispose();
        }
        passive_nodes_.clear();
        for (var e0 : positive_nodes.entrySet()) {
            e0.getValue().dispose();
        }

    }

    @Override
    public void setMyNode(final ServerNode.server_node myNode) {
        my_node_info_ = myNode;
    }

    @Override
    public final ServerNode.server_node getMyNode() {
        return my_node_info_;
    }

    public void register(INode nodeHandle, IChannel channelHandle) {
        String nodeID = nodeHandle.getNodeID();
        INode node = nodeHandle;
        if (passive_nodes_.containsKey(nodeID))
            node = passive_nodes_.get(nodeID);
        else
            passive_nodes_.put(nodeID, node);

        if (channelHandle != null)
            node.addChannel(channelHandle);
    }

    public void unregister(String nodeID) {
        if (!passive_nodes_.containsKey(nodeID))
            return;
        passive_nodes_.remove(nodeID);
    }

    public void removePositive(String nodeID) {
        if (!positive_nodes.containsKey(nodeID))
            return;
        positive_nodes.remove(nodeID);
    }

    @Override
    public INode getNode(String nodeID) {
        if (passive_nodes_.containsKey(nodeID))
            return passive_nodes_.get(nodeID);
        return null;
    }

    public static String getServerId(String hsource) {
        return hsource.substring(0, hsource.lastIndexOf(NodeConstant.NODE_ID_SEP));
    }

    public static String wrapNodeId(String hsource, String simple_node_id) {
        return getServerId(hsource) + NodeConstant.NODE_ID_SEP + simple_node_id;
    }

    private class NodetypeCheck {
        int idx;
        String prefix;
        char nodetype;

        private NodetypeCheck(String wrapNodeId) {
            this.idx = wrapNodeId.lastIndexOf(NodeConstant.NODE_ID_SEP);
            this.prefix = wrapNodeId.substring(0, this.idx + 1);
            this.nodetype = wrapNodeId.charAt(idx + 1);
        }
    }

    private boolean checkNodetype(NodetypeCheck dst, NodetypeCheck src) {
        return dst.nodetype == src.nodetype && dst.prefix.matches(src.prefix);
    }

    private boolean checkNodetype(String nodeId, char nodetype) {
        return nodeId.charAt(nodeId.lastIndexOf(NodeConstant.NODE_ID_SEP) + 1) == nodetype;
    }

    public List<INode> getNodesWithType(String hsource, final char nodeType) {
        NodetypeCheck dc = new NodetypeCheck(hsource.substring(0, hsource.lastIndexOf(NodeConstant.NODE_ID_SEP) + 1) + nodeType);

        List<INode> nodesResult = new LinkedList<>();
        for (var e0 : passive_nodes_.entrySet()) {
            if (e0.getValue() == null)
                continue;
            NodetypeCheck sc = new NodetypeCheck(e0.getKey());
            if (this.checkNodetype(dc, sc)) {
                nodesResult.add(e0.getValue());
            }
        }
        return nodesResult;
    }

    @Override
    public List<INode> getNodesWithType(final char nodeType) {
        List<INode> nodesResult = new LinkedList<>();
        for (var e0 : passive_nodes_.entrySet()) {
            if (e0.getValue() == null)
                continue;
            if (e0.getValue().getType() == nodeType) {
                //if (this.checkNodetype(e0.getKey(), nodeType)) {
                nodesResult.add(e0.getValue());
            }
        }
        return nodesResult;
    }

    @Override
    public INode getNodeWithType(final char nodeType) {
        List<INode> nodesResult = this.getNodesWithType(nodeType);

        if (nodesResult.size() == 0) return null;
        return nodesResult.get(ThreadLocalRandom.current().nextInt(nodesResult.size()));
    }

    @Override
    public ICheckerManager getChecker() {
        return checker_;
    }

    public final void connect() {
        List<NodeConfig> allConfig = ManagerService.getRegister().getAllConfig(my_node_info_.getNodeId());
        log.info("集群配置：{}", JSON.toJSONString(allConfig));
        for (NodeConfig node : allConfig) {
            //网关之间不互相连接
            /*if (isGateWay(node) && my_node_info_.getType().equals(String.valueOf(NodeConstant.kGatewayService))) {
                continue;
            }*/

            //自己节点跳过
            if (this.my_node_info_.getNodeId().equals(node.getNodeId())) {
                continue;
            }

            //校验是否已经有链接（其他服务器已经连接到本节点服务器），有则跳过
            if (passive_nodes_.containsKey(node.getNodeId())) {
                log.info("Skip this node because a connection has already been established with it，nodeId={}", node.getNodeId());
                continue;
            }

            try {
                this.connect(node.getIp(), node.getPort(), node.getNodeId(), node.getMaxChannel());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public final CenterNode connect(final String peerAddress,
                                    final int peerPort,
                                    final String peerNodeID,
                                    final int maxChannel) {
        return this.connect(getMyNodeID(), peerAddress, peerPort, peerNodeID, maxChannel);
    }

    public final CenterNode connect(String myNode, final String peerAddress,
                                    final int peerPort,
                                    final String peerNodeID,
                                    final int maxChannel) {
        CenterNode node = new CenterNode(myNode, peerNodeID,
                peerAddress,
                peerPort,
                maxChannel, this);

        this.positive_nodes.put(peerNodeID, node);
        node.start();
        return node;
    }

    public final boolean isRegistered() {
        Collection<CenterNode> values = positive_nodes.values();
        if (values != null && values.size() > 0) {
            for (CenterNode value : values) {
                if (!value.isRegistered()) {
                    return false;
                }
            }
        }

        return true;
    }

    public Map<String, INode> getAllNodes() {
        return passive_nodes_;
    }

    public CenterNode getPositiveNode(String node_id) {
        return this.positive_nodes.get(node_id);
    }

    public boolean isPositive(String node_id) {
        return this.positive_nodes.containsKey(node_id);
    }

    private boolean isGateWay(NodeConfig config) {
        return (config != null && NodeConstant.kGatewayService == config.getType().charAt(0));
    }
}