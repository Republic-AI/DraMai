package com.infinity.common.msg.platform.live;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//切换npc返回消息
public class QueryGiftResponse extends BaseMsg<List<GiftData>> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.QUERY_GIFT_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.QUERY_GIFT_COMMAND;
    }
}
