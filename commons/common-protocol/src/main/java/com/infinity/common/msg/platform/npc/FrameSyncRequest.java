package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//帧同步
public class FrameSyncRequest extends BaseMsg<FrameSyncRequest.RequestData> {
    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.FRAME_SYNC_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.FRAME_SYNC_COMMAND;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestData {
        private long npcId;
        //x轴坐标
        private int x;
        //x轴坐标
        private int y;
    }
}
