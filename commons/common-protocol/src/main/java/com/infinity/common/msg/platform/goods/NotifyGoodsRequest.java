package com.infinity.common.msg.platform.goods;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.goods.GoodsData;

import java.util.List;

//服务端推送背包道具给客户端
public class NotifyGoodsRequest extends BaseMsg<List<GoodsData>> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kNotifyGoodsCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kNotifyGoodsCommand;
    }
}
