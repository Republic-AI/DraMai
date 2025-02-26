package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

//玩家签到
public class SignResponse extends BaseMsg<SignResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kSignCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kSignCommand;
    }

    @Data
    public static class ResponseData {
        //是否能签到:0:否，1：是，连续三天签到后就不能再签到
        private int sign;
        //签到3次获得的道具类型：0: 无, 1:猫，2:积分
        private int itemType;
        //如果itemType是猫，value=猫ID，itemType积分，则value=积分
        private int itemValue;
    }
}
