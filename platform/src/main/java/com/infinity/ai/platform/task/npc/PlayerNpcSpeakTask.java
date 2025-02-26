package com.infinity.ai.platform.task.npc;

import com.infinity.ai.domain.tables.PlayerNpc;
import com.infinity.ai.platform.manager.NpcHolder;
import com.infinity.ai.platform.manager.NpcManager;
import com.infinity.ai.platform.manager.Player;
import com.infinity.ai.platform.manager.PlayerManager;
import com.infinity.ai.platform.npc.goap.action.ActionEnumType;
import com.infinity.ai.platform.npc.goap.action.SpeakAction;
import com.infinity.common.base.exception.ResultCode;
import com.infinity.common.consts.ErrorCode;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.player.PlayerMoveRequest;
import com.infinity.common.msg.platform.player.PlayerSpeakRequest;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 玩家NPC说话消息
 */
@Slf4j
public class PlayerNpcSpeakTask extends BaseTask<PlayerSpeakRequest> {

    @Override
    public int getCommandID() {
        return ProtocolCommon.PLAYER_SPEAK_COMMAND;
    }

    @Override
    public boolean run0() {
        PlayerSpeakRequest msg = this.getMsg();
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
        new SpeakAction(null).sendActionMessage(npcHolder.getNpc(), null, ActionEnumType.Speak.getCode(), "npcId", npcHolder.getNpc().getId(), "content", msg.getData().getContent());
        return true;
    }

}
