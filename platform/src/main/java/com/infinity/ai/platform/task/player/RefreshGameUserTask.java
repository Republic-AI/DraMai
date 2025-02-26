package com.infinity.ai.platform.task.player;

import com.infinity.ai.platform.application.Config;
import com.infinity.common.base.data.GameUserMgr;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.cluster.RefreshPlayerMessage;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户登录后刷新本地用户信息
 */
@Slf4j
public class RefreshGameUserTask extends BaseTask<RefreshPlayerMessage> {

    public RefreshGameUserTask() {
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.MSG_CODE_REFRESH_GAMEUSER;
    }

    @Override
    public boolean run0() {
        log.debug(">>>>refresh GameUser, msg={}", this.getMsg());
        RefreshPlayerMessage msg = this.getMsg();
        if (!Config.getInstance().getNodeId().equals(msg.getSourceServiceId())) {
            switch (msg.getOperate()) {
                case 0:
                    GameUserMgr.refreshGameUser(msg.getUserId());
                    break;
                case 1:
                    log.debug("RefreshGameUserTask removeGameUser, playerId={}", msg.getUserId());
                    GameUserMgr.removeGameUser(msg.getUserId(), null);
                    break;
                default:
                    break;
            }
        }

        return true;
    }

}
