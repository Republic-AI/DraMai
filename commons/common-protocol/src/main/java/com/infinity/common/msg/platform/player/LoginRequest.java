package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

//登录消息
public class LoginRequest extends BaseMsg<LoginRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.LOGIN_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.LOGIN_COMMAND;
    }

    @Data
    public static class RequestData {
        //登录类型 0:游客模式,1:钱包登陆
        private int loginType;
        //登录账号
        private String name;
        //密码 :1:验证码 ,2:密码, 3:其他 第三方登录为第三方accessToken
        private String password;
        //昵称
        private String nickName;
        //头像
        private String avatar;
        //性别：0:未知,1:男,2:女
        private int sex;
        //国际时区
        private int timeZone;
        //设备操作系统
        private String clientOs;

        /*//用户系统用户ID
        private String userId;
        //用户的邀请码
        private String inviteCode;
        //邀请人ID：
        private String invite;
        //用户系统token
        private String token;*/
    }
}
