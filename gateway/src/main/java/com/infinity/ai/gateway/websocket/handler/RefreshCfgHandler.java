package com.infinity.ai.gateway.websocket.handler;

import com.infinity.ai.gateway.websocket.GatewayConfig;
import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RefreshCfgHandler implements IResponseHandler {
    @Override
    public int getCommand() {
        return ProtocolCommon.SYS_REFRESH_COMMAND;
    }

    @Override
    public void preHandler(BaseMsg msg) {

    }

    @Override
    public void hander(BaseMsg msg) {
        log.info("refresh config ......");
        GatewayConfig.getInstance().init();
    }

    @Override
    public void afterHandler(BaseMsg msg) {

    }
}
