package com.infinity.network;

import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.manager.node.INode;
import com.infinity.manager.node.NodeConstant;
import com.infinity.manager.node.NodeManager;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class QueueSelector {

    private static class Holder {
        private static final QueueSelector kInstance = new QueueSelector();
    }

    public static QueueSelector getInstance() {
        return Holder.kInstance;
    }

    public IChannel queue(BaseMsg msg) {
        IChannel channel = null;
        GameUser gameUser = msg.getPlayerId() == null ? null : GameUserMgr.getGameUser(msg.getPlayerId());
        switch (msg.getType()) {
            case ProtocolCommon.MSG_TYPE_PLATFORM:
                if (gameUser == null || StringUtils.isBlank(gameUser.getPlatformServiceId())) {
                    channel = loadBalance(msg.getPlayerId(), NodeConstant.kPlatformService);
                } else {
                    channel = findUserService(gameUser.getPlatformServiceId(), msg.getPlayerId(), NodeConstant.kPlatformService);
                }
                break;
            case ProtocolCommon.MSG_TYPE_CHAT:
                if (gameUser == null || StringUtils.isBlank(gameUser.getChatServiceId())) {
                    channel = loadBalance(msg.getPlayerId(), NodeConstant.kChatService);
                } else {
                    channel = findUserService(gameUser.getChatServiceId(), msg.getPlayerId(), NodeConstant.kChatService);
                }
                break;
            default:
                break;
        }
        return channel;
    }

    public IChannel loadBalance(Long playerId, char nodeType) {
        List<INode> nodes = NodeManager.getInstance().getNodesWithType(nodeType);
        if (nodes == null || nodes.size() == 0) {
            return null;
        }

        return nodes.size() == 1 ? nodes.get(0).getChannel() : nodes.get(getIndex(playerId, nodes.size())).getChannel();
    }

    public IChannel findUserService(String userServiceId, Long playerId, char nodeType) {
        INode node = NodeManager.getInstance().getNode(userServiceId);
        return node == null ? loadBalance(playerId, nodeType) : node.getChannel();
    }

    private static int getIndex(Long playerId, int size) {
        int index = playerId.hashCode() % size;
        return index < 0 ? 0 : index;
    }
}
