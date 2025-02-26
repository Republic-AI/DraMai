package com.infinity.common.msg.platform.goods;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

//查询道具
public class TypeToEarnReponse extends BaseMsg<TypeToEarnReponse.ReponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.TYPE_TO_EARN_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.TYPE_TO_EARN_COMMAND;
    }

    @Data
    public static class ReponseData {

        private int type;

        private Long npcId;
    }
}
