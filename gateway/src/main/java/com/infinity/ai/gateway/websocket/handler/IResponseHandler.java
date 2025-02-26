package com.infinity.ai.gateway.websocket.handler;

import com.infinity.common.msg.BaseMsg;

public interface IResponseHandler {
    int getCommand();

    void preHandler(BaseMsg msg);

    void hander(BaseMsg msg);

    void afterHandler(BaseMsg msg);
}
