package com.infinity.common.msg.platform.live;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//送礼
public class SendGiftRequest extends BaseMsg<SendGiftRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.SEND_GIFT_COMMAND;
    }

    public static int getCmd() {
        return ProtocolCommon.SEND_GIFT_COMMAND;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RequestData {
        //房间ID=NPCID
        private Long roomId;
        //礼物 ID
        public int giftId;
        //数量
        public long num;
    }
}
