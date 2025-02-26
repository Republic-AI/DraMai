package com.infinity.manager.node;

import com.infinity.protocol.ServerNode;

import java.util.List;

public interface INodeManager {
    void init();

    void fini();

    ServerNode.server_node getMyNode();

    void setMyNode(final ServerNode.server_node myNode);

    INode getNode(final String nodeID);

    List<INode> getNodesWithType(final char nodeType);

    INode getNodeWithType(final char nodeType);

    ICheckerManager getChecker();
}