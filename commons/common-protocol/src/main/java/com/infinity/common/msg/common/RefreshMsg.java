package com.infinity.common.msg.common;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.utils.GsonUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshMsg extends BaseMsg {
    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.SYS_REFRESH_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.SYS_REFRESH_COMMAND;
    }

    @Override
    public String toString() {
        return GsonUtil.parseObject(this);
    }
}
