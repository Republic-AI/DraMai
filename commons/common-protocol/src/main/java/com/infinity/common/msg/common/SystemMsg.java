package com.infinity.common.msg.common;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.utils.GsonUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemMsg extends BaseMsg {
    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.KICK_LINE_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.KICK_LINE_COMMAND;
    }

    @Override
    public String toString() {
        return GsonUtil.parseObject(this);
    }
}
