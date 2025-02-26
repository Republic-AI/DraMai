package com.infinity.ai.login.task.system;

import com.infinity.ai.login.application.Config;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.common.RefreshMsg;
import com.infinity.manager.task.BaseTask;
import lombok.extern.slf4j.Slf4j;

/**
 * 刷新配置
 */
@Slf4j
public class RefreshTask extends BaseTask<RefreshMsg> {

    public RefreshTask() {
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.SYS_REFRESH_COMMAND;
    }

    @Override
    public boolean run0() {
        log.info("refresh config......");
        Config.getInstance().reload();
        return true;
    }
}
