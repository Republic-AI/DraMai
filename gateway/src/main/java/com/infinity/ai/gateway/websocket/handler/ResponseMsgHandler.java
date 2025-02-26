package com.infinity.ai.gateway.websocket.handler;

import com.infinity.ai.gateway.websocket.MessageHelper;
import com.infinity.common.msg.BaseMsg;

public class ResponseMsgHandler implements IResponseHandler {

    @Override
    public int getCommand() {
        return -1;
    }

    @Override
    public void preHandler(BaseMsg msg) {

    }

    @Override
    public void hander(BaseMsg msg) {
        MessageHelper.outMsgToClient(msg);
    }

    @Override
    public void afterHandler(BaseMsg msg) {

    }
}
