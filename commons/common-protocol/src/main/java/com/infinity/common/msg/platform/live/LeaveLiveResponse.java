package com.infinity.common.msg.platform.live;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.NpcData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//切换npc返回消息
public class LeaveLiveResponse extends BaseMsg<LeaveLiveResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.LEAVE_LIVE_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.LEAVE_LIVE_COMMAND;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseData {
        //NPC ID
        public Integer roomId;
    }
}
