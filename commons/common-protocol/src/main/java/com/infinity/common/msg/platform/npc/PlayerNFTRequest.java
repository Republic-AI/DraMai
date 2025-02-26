package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

public class PlayerNFTRequest extends BaseMsg<PlayerNFTRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.PLAYER_NFT_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.PLAYER_NFT_COMMAND;
    }

    @Data
    public static class RequestData {

        private int id; // 1 个人发言 2 聊天

        private int op;// 1 互动 2 拾取

        private String address;//钱包地址
    }
}
