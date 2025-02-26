package com.infinity.common.msg.platform.login;

import com.infinity.common.msg.BaseMsg;
import lombok.Data;

public class UserLoginRequest extends BaseMsg<UserLoginRequest.RequestData> {

    @Data
    public static class RequestData {
        private String userId;
    }
}
