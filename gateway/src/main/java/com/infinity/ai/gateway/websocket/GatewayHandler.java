package com.infinity.ai.gateway.websocket;

import com.infinity.ai.gateway.websocket.handler.*;
import com.infinity.common.msg.ProtocolCommon;

import java.util.HashMap;
import java.util.Map;

public class GatewayHandler {
    private Map<Integer, IResponseHandler> handlerMap = new HashMap<>();

    private GatewayHandler() {
        handlerMap.put(ProtocolCommon.LOGIN_COMMAND, new LoginMsgHandler());
        handlerMap.put(ProtocolCommon.OFF_LINE_COMMAND, new LogoutMsgHandler());
        handlerMap.put(ProtocolCommon.MSG_CODE_REFRESH_GAMEUSER, new RefreshGameUserHandler());
        handlerMap.put(ProtocolCommon.SYS_REFRESH_COMMAND, new RefreshCfgHandler());
        handlerMap.put(ProtocolCommon.MSG_BROADCAST, new BroadcastMsgHandler());
        handlerMap.put(ProtocolCommon.FRAME_SYNC_COMMAND, new BroadcastMsgHandler());
        handlerMap.put(-1, new ResponseMsgHandler());
    }

    private static class Holder {
        private static final GatewayHandler kInstance = new GatewayHandler();
    }

    public static GatewayHandler getInstance() {
        return Holder.kInstance;
    }

    public IResponseHandler getHandler(int code, int command) {
        return (code != 0) ? handlerMap.get(-1) : handlerMap.getOrDefault(command, handlerMap.get(-1));
    }

}
