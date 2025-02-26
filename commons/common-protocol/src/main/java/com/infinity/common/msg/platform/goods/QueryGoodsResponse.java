package com.infinity.common.msg.platform.goods;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.goods.GoodsData;

import java.util.List;

//查询背包道具
public class QueryGoodsResponse extends BaseMsg<List<GoodsData>> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kGoodsSelectCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kGoodsSelectCommand;
    }
}
