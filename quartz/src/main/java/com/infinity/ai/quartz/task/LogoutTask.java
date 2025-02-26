package com.infinity.ai.quartz.task;

import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.player.LogoutRequest;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户退出或者掉线
 */
@Slf4j
public class LogoutTask extends BaseTask<LogoutRequest> {

    public LogoutTask() {
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.OFF_LINE_COMMAND;
    }

    @Override
    public boolean run0() {
        //LogoutRequest msg = this.getMsg();
        //log.debug("player logout,msg={}", msg.toString());
        //GameUserMgr.removeGameUser(msg.getPlayerId(), null);
        return true;
    }
}
