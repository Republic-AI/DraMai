package com.infinity.common.msg.platform.npc;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

import java.util.Map;

public class PlayerNFTResponse extends BaseMsg<PlayerNFTResponse.ResponseData> {

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
    public static class ResponseData {

        private NFTData nftInfo;
    }
}
