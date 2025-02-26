package com.infinity.ai.platform.npc.live;

import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.common.consts.RedisKeyEnum;
import com.infinity.common.utils.spring.SpringContextHolder;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;

import java.util.Collection;
import java.util.function.Function;

public class Room {

    //切换房间
    public void enterRoom(long playerId, long roomId) {
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            return;
        }

        Long oldRoomId = player.getPlayerModel().get_v().getLive().getRoomId();
        addPlayerToRoom(player, roomId);
        removePlayerFromRoom(playerId, oldRoomId);
    }

    public void addPlayerToRoom(Player player, long roomId) {
        RedisKeyEnum liveRoom = RedisKeyEnum.LIVE_ROOM;
        RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
        RScoredSortedSet<Long> room = redissonClient.getScoredSortedSet(liveRoom.getKey(roomId), liveRoom.codec);
        room.add(System.currentTimeMillis(), player.getPlayerID());
        player.getPlayerModel().get_v().getLive().setRoomId(roomId);

        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(roomId);
        NPC npc = npcHolder.getNpc();
        npc.getPlayerIds().add(player.getPlayerID());
    }

    public void removePlayerFromRoom(long playerId, long oldRoomId) {
        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(oldRoomId);
        NPC npc = npcHolder.getNpc();
        npc.getPlayerIds().remove(oldRoomId);

        if (oldRoomId > 0) {
            RedisKeyEnum liveRoom = RedisKeyEnum.LIVE_ROOM;
            RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
            RScoredSortedSet<Long> oldRoom = redissonClient.getScoredSortedSet(liveRoom.getKey(oldRoomId), liveRoom.codec);
            oldRoom.remove(playerId);
        }
    }

    public void forEachNotify(long roomId, Function<Long, Void> onNotifyEvent) {
        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(roomId);
        NPC npc = npcHolder.getNpc();
        npc.getPlayerIds().forEach(playerId -> {
            if (onNotifyEvent != null) {
                onNotifyEvent.apply(playerId);
            }
        });
    }

    public void init(long roomId) {
        try {
            NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(roomId);
            NPC npc = npcHolder.getNpc();

            int pageSize = 100;
            RedisKeyEnum liveRoom = RedisKeyEnum.LIVE_ROOM;
            RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
            RScoredSortedSet<Long> room = redissonClient.getScoredSortedSet(liveRoom.getKey(roomId), liveRoom.codec);

            int total = room.size();
            int totalPage = (total % pageSize == 0) ? total / pageSize : (total / pageSize + 1);
            for (int i = 1; i <= totalPage; i++) {
                int offset = (i - 1) * pageSize;
                Collection<Long> playerIds = room.valueRangeReversed(0, true, Double.MAX_VALUE, true, offset, pageSize);
                if (playerIds != null && playerIds.size() > 0) {
                    npc.getPlayerIds().addAll(playerIds);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static class Holder {
        private static final Room INSTANCE = new Room();
    }

    public static Room getInstance() {
        return Room.Holder.INSTANCE;
    }
}
