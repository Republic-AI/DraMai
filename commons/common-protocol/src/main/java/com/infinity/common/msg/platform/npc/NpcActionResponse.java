package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

//登出
public class NpcActionResponse extends BaseMsg<Map<String, Object>> {

    //NPC ID，非空
    @Getter
    @Setter
    private Long npcId;

    //Action ID，非空
    @Getter
    @Setter
    private int actionId;

    @Setter
    @Getter
    private Long ack;

    @Setter
    @Getter
    private Long actionUid;

    @Setter
    @Getter
    private int status;//行为状态

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.NPC_ACTION_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.NPC_ACTION_COMMAND;
    }
}
