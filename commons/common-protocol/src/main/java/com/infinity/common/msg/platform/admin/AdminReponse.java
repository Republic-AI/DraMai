package com.infinity.common.msg.platform.admin;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AdminReponse extends BaseMsg<AdminReponse.ResponseData> {
    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.KAdminCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.KAdminCommand;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseData {
        //GM 命令
        public String content;
    }
}
