package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.Map;
import java.util.Set;

//服务端推送NPC行为到客户端（广播消息）
public class NpcActionBroadRequest extends BaseMsg<NpcActionBroadRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NPC_ACTION_BROAD_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.NPC_ACTION_BROAD_COMMAND;
    }

    @Data
    public static class RequestData {
        //NPC ID，非空
        private Long npcId;
        //Action ID，非空
        private int actionId;
        //行为ID
        private long bid;
        //指定回复的用户ID
        private Set<String> users;
        //行为数据，可空
        private Map<String, Object> params;
        //行为开始时间
        private long startTime;
        //行为结束时间
        private long endTime;
        //行为心情
        private String mood;

    }
}
