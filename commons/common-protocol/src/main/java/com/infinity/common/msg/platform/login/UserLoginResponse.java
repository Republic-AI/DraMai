package com.infinity.common.msg.platform.login;

import com.infinity.common.msg.BaseMsg;
import lombok.Data;

public class UserLoginResponse extends BaseMsg<UserLoginResponse.ResponseData> {

    @Data
    public static class ResponseData {

        private String userId;

        private int status;// 0 游客 1 绑定用户
    }
}
