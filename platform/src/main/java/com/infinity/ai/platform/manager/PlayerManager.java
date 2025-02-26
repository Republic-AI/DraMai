package com.infinity.ai.platform.manager;

import com.infinity.ai.platform.application.Config;
import com.infinity.ai.PPlayer;
import com.infinity.common.base.data.GameUser;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.cluster.RefreshPlayerMessage;
import com.infinity.db.db.DBManager;
import com.infinity.network.MessageSender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 在线玩家列表管理,在线玩家的节点管理
 * TODO: 需要把在线玩家保存到Redis里面，以便其他节点和重启时候的读取
 */
public class PlayerManager {
    //在线玩家列表
    private final Map<Long, Player> onlinePlayer = new ConcurrentHashMap<>();
    //在线玩家所在的gateway节点关系
    private final Lock relation_locker_ = new ReentrantLock();
    //private final Map<Long, PlayerNodeRelation> player_node_relation_ = new HashMap<>();
    //private final Map<String, PlayerNodeRelation> node_player_relation_ = new HashMap<>();

    private boolean exit_ = false;

    private static class PlayerManagerHolder {
        private static final PlayerManager kInstance = new PlayerManager();
    }

    public static PlayerManager getInstance() {
        return PlayerManagerHolder.kInstance;
    }

    private PlayerManager() {
        //Threads.addListener(ThreadConst.TIMER_1S, "PlayerManager.OnLoopEvent", this::OnLoopEvent);
    }

    public void dispose() {
        exit_ = true;
    }

    private void checkPlayers() {
        for (Player player : onlinePlayer.values()) {
            if (exit_) {
                break;
            }

            if (player != null) {
                //player.check();
            }
        }
    }


    public boolean OnLoopEvent(int interval) {
        if (exit_)
            return true;

        checkPlayers();
        return false;
    }

    public boolean hasPlayerWithID(final long playerID) {
        Player onlinePlayer = this.getOnlinePlayerWithID(playerID);
        if (onlinePlayer == null) {
            PPlayer p = DBManager.get(PPlayer.class, playerID);
            return p != null;
        }

        return true;
    }

    public Player getOnlinePlayerWithID(final long playerID) {
        Player player = onlinePlayer.get(playerID);
        GameUser gameUser;
        if (player == null && (gameUser = GameUserMgr.getGameUser(playerID)) != null) {
            gameUser.setPlatformServiceId(Config.getInstance().getNodeId());
            GameUserMgr.addGameUser(gameUser, () -> {
                RefreshPlayerMessage refreshPlayerMessage = new RefreshPlayerMessage();
                refreshPlayerMessage.setUserId(playerID);
                refreshPlayerMessage.setSourceServiceId(Config.getInstance().getNodeId());
                MessageSender.getInstance().broadcastMessageToAllService(refreshPlayerMessage);
            });

            PPlayer dbPlayer = DBManager.get(PPlayer.class, playerID);
            if(dbPlayer != null){
                return addOnlinePlayer(playerID, dbPlayer);
            }
        }

        return player;
    }

    public Player getOnlinePlayer(final long playerID) {
        Player player = onlinePlayer.get(playerID);
        return player;
    }

    public Player addOnlinePlayer(final long playerID, final PPlayer dbPlayer) {
        Player newOnlinePlayer = new Player(playerID, dbPlayer);
        onlinePlayer.put(playerID, newOnlinePlayer);
        return newOnlinePlayer;
    }

    public void removeOnlinePlayer(final long playerID) {
        Player player = onlinePlayer.remove(playerID);
        removePlayerRelationWithPlayerID(playerID);
        if (player != null) {
            player.dispose();
        }
    }

    /*public final void putPlayerRelation(final long playerID, final String clientNodeId, final String gatewayNodeId) {
        try (final LockGuard<Lock> guarder = new LockGuard<Lock>(relation_locker_)) {
            PlayerNodeRelation playerNodeRelation = player_node_relation_.get(playerID);
            if (playerNodeRelation == null) {
                playerNodeRelation = new PlayerNodeRelation(playerID, clientNodeId, gatewayNodeId);
                player_node_relation_.put(playerID, playerNodeRelation);
                node_player_relation_.put(clientNodeId, playerNodeRelation);
            } else {
                playerNodeRelation.updateNodeID(clientNodeId);
                playerNodeRelation.updateGatewayNodeID(gatewayNodeId);
            }
        }
    }

    public final Pair<String, String> getPlayerRelation(final long playerID) {
        try (final LockGuard<Lock> guarder = new LockGuard<Lock>(relation_locker_)) {
            final PlayerNodeRelation relation = player_node_relation_.get(playerID);
            if (relation == null) {
                return null;
            }

            return new Pair<>(relation.getNodeID(), relation.getGatewayNodeID());
        }
    }

    public final PlayerNodeRelation getPlayerRelationWithPlayerID(final long playerID) {
        try (final LockGuard<Lock> guarder = new LockGuard<Lock>(relation_locker_)) {
            return player_node_relation_.get(playerID);
        }
    }

    public final PlayerNodeRelation getPlayerRelationWithNodeID(final String clientNodeID) {
        try (final LockGuard<Lock> guarder = new LockGuard<Lock>(relation_locker_)) {
            return node_player_relation_.get(clientNodeID);
        }
    }
*/
    public final void removePlayerRelationWithPlayerID(final long playerID) {
        this.removePlayerRelation(playerID);
    }

    public final void removePlayerRelationWithNodeID(final String nodeID) {
        /*try (final LockGuard<Lock> guarder = new LockGuard<>(relation_locker_)) {
            final PlayerNodeRelation relation = node_player_relation_.remove(nodeID);
            if (relation != null) {
                player_node_relation_.remove(relation.getPlayerID());
                relation.dispose();
            }
        }*/
    }

    public final void removePlayerRelation(final long playerID) {
        /*try (final LockGuard<Lock> guarder = new LockGuard<>(relation_locker_)) {
            final PlayerNodeRelation relation = player_node_relation_.remove(playerID);
            if (relation != null) {
                node_player_relation_.remove(relation.getNodeID());
                relation.dispose();
            }
        }*/
    }

    // 仅仅将一个玩家移出 管理器列表,不需要做存盘处理
    public final void justRemove(long playerID) {
        /*try (final LockGuard<Lock> guarder = new LockGuard<>(relation_locker_)) {
            final PlayerNodeRelation relation = player_node_relation_.remove(playerID);
            if (relation != null) {
                node_player_relation_.remove(relation.getNodeID());
            }
        }*/

        onlinePlayer.remove(playerID);
    }

    public void newNotifyPlayer(Player player) {
        assert player != null;
        if (player == null) {
            return;
        }

        /*final long playerID = player.getPlayerID();
        final PPlayer dbPlayer = player.getPlayerModel();
        final Pair<IChannel, String> nodeInfo = this.getNodeChannel(playerID);
        if (nodeInfo == null) {
            return;
        }

        ITask playerTask = new NotifyPlayerTask(dbPlayer, nodeInfo);
        playerTask.setChannel(nodeInfo.first);
        playerTask.init();
        ManagerService.getTaskManager().add(playerTask);*/
    }

    /*public Pair<IChannel, String> getNodeChannel(final long playerID) {
        final Pair<String, String> nodeRelation = getPlayerRelation(playerID);
        if (nodeRelation == null) {
            assert nodeRelation != null;
            return null;
        }

        final String clientNodeId = nodeRelation.first;
        final String gatewayNodeId = nodeRelation.second;

        INode gatewayNode = NodeManager.getInstance().getNode(gatewayNodeId);
        assert gatewayNode != null;
        IChannel gatewayChannel = gatewayNode.getChannel();

        if (gatewayChannel == null) {
            LoggerHelper.error("failed to get channel. playerId=%s,gatewayNodeID=%s,gatewayChannelSize=%d",
                    playerID, gatewayNodeId, gatewayNode.getChannel());
            assert gatewayChannel != null;

            return null;
        }
        return new Pair<>(gatewayChannel, clientNodeId);
    }*/

    /**
     * 返回所有在线的玩家
     *
     * @return
     */
    public Map<Long, Player> getOnlinePlayerMap() {
        return onlinePlayer;
    }

    public Player getOnlinePlayer() {
        for (Player player : onlinePlayer.values()) {
            return player;
        }
        return null;
    }

    /**
     * 获取一个玩家,忽略是否在线
     *
     * @return
     */
    public Player getPlayerIgnoreOnline(long playerId) {
        Player player = this.getOnlinePlayerWithID(playerId);
        if (player == null) {
            // 不在线,查看是否有这个玩家
            PPlayer pPlayer = DBManager.get(PPlayer.class, playerId);
            if (pPlayer == null) {
                return null;
            }
            player = new Player(playerId);
        }
        return player;
    }
}


