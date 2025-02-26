package com.infinity.common.msg.platform.player;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

//角色设置
public class CharaterSetRequest extends BaseMsg<CharaterSetRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.CHARATER_SET_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.CHARATER_SET_COMMAND;
    }

    @Data
    public static class RequestData {
        //角色模型
        private int model;
        //角色名称
        private String name;
        //职业
        private String career;
        //关键词
        private String keyword;
        //发型
        private int hair;
        //top
        private int top;
        //bottoms
        private int bottoms;
        //支付信息
        private String payInfo;
    }
}
