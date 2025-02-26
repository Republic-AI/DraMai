package com.infinity.ai.platform.task.live;

import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.npc.live.Room;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.consts.RedisKeyEnum;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.live.SwithLiveRequest;
import com.infinity.common.utils.spring.SpringContextHolder;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSortedSet;
import org.redisson.api.RedissonClient;

/**
 * 切换NPC房间
 */
@Slf4j
public class SwithLiveTask extends BaseTask<SwithLiveRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.SWITH_LIVE_COMMAND;
    }

    @Override
    public boolean run0() {
        SwithLiveRequest msg = this.getMsg();
        log.debug("SwithLiveTask,msg={}", msg.toString());

        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("Swith Live error, playerId params error,playerId={}", playerId);
            return false;
        }

        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            sendErrorMsg(ErrorCode.PlayerNotOnlineError, ErrorCode.PlayerNotOnlieErrorMessage, msg);
            return false;
        }

        Room.getInstance().enterRoom(playerId, msg.getData().getNpcId());
        return true;
    }

    private void enterRoom(Player player) {
        SwithLiveRequest msg = this.getMsg();
        long roomId = msg.getData().getNpcId();

        RedisKeyEnum liveRoom = RedisKeyEnum.LIVE_ROOM;
        RedissonClient redissonClient = SpringContextHolder.getBean(RedissonClient.class);
        RSortedSet<Long> room = redissonClient.getSortedSet(liveRoom.getKey(roomId), liveRoom.codec);
        room.add(player.getPlayerID());

        Long oldRoomId = player.getPlayerModel().get_v().getLive().getRoomId();
        player.getPlayerModel().get_v().getLive().setRoomId(roomId);

        if (oldRoomId > 0) {
            RSortedSet<Long> oldRoom = redissonClient.getSortedSet(liveRoom.getKey(oldRoomId), liveRoom.codec);
            oldRoom.remove(oldRoomId);
        }
    }
}
