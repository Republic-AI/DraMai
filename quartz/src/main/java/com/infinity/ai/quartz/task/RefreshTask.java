package com.infinity.ai.quartz.task;

import com.infinity.ai.quartz.config.QuartzConfig;
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
        QuartzConfig.getInstance().init();
        return true;
    }
}
