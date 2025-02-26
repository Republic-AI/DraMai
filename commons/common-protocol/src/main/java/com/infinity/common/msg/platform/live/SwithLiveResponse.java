package com.infinity.common.msg.platform.live;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//切换npc返回消息
public class SwithLiveResponse extends BaseMsg<SwithLiveResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.SWITH_LIVE_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.SWITH_LIVE_COMMAND;
    }

    @Data
    @AllArgsConstructor
    //@NoArgsConstructor
    @Builder
    public static class ResponseData {
    }
}
