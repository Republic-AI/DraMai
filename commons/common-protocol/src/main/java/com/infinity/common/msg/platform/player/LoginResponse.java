package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.Map;

//登录返回消息
public class LoginResponse extends BaseMsg<LoginResponse.ResponseData> {

    @Data
    public static class ResponseData {
        // 玩家验签信息
        private String token;
        //服务器时间戳
        private long timestamp = 5;
        //玩家信息
        private PlayerData player;
        //玩家twitter信息
        private TwitterData twitterData;
        //登陆类型
        private int loginType;
    }

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.LOGIN_COMMAND;
    }

    public static int getCmd(){
        return ProtocolCommon.LOGIN_COMMAND;
    }
}
