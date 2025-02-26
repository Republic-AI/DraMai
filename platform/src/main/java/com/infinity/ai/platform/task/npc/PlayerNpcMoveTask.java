package com.infinity.ai.platform.task.npc;

import com.infinity.ai.domain.tables.PlayerNpc;
import com.infinity.ai.platform.manager.*;
import com.infinity.ai.platform.map.Position;
import com.infinity.ai.platform.npc.NPC;
import com.infinity.ai.platform.npc.goap.action.ActionEnumType;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.player.PlayerMoveRequest;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 玩家NPC移动消息
 */
@Slf4j
public class PlayerNpcMoveTask extends BaseTask<PlayerMoveRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.PLAYER_MOVE_COMMAND;
    }

    @Override
    public boolean run0() {
        PlayerMoveRequest msg = this.getMsg();
        long playerId = msg.getPlayerId();
        if (playerId <= 0) {
            log.error("CatModifyTask error, playerId params error,playerId={}", playerId);
            return true;
        }

        //校验用户是否在线
        Player player = PlayerManager.getInstance().getOnlinePlayerWithID(playerId);
        if (player == null) {
            //用户不在线
            sendErrorMsg(ErrorCode.PlayerNotOnlineError, ErrorCode.PlayerNotOnlieErrorMessage, msg);
            return true;
        }
        PlayerNpc pNpc = player.getPlayerModel().get_v().getNpc();
        if (pNpc == null) {
            //用户不在线
            sendErrorMsg(ErrorCode.PlayerNotOnlineError, ErrorCode.PlayerNotOnlieErrorMessage, msg);
            return true;
        }
        NpcHolder npcHolder = NpcManager.getInstance().getOnlineNpcHolder(pNpc.getNpcId());
        if (npcHolder == null) {
            log.debug("not found on line npc,npcId={}", pNpc.getNpcId());
            sendMessage(buildError(ResultCode.NPC_NOT_EXIST_ERROR, msg));
            return false;
        }
        if (!MapDataManager.getInstance().getGameMap().isWalkable(new Position(msg.getData().getGridX(), msg.getData().getGridY()))) {
            sendErrorMsg(ErrorCode.PositionNotWalkableError, ErrorCode.PositionNotWalkableErrorMessage, msg);
            return true;
        }
        NPC npc = npcHolder.getNpc();
        npc.clear();
        Map<String, Object> params = new HashMap<>();
        params.put("gridX", msg.getData().getGridX());
        params.put("gridY", msg.getData().getGridY());
        npc.doAction(ActionEnumType.Move.getCode(), params, true);
        return true;
    }

}
