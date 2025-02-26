package com.infinity.common.msg.platform.task;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.goods.GoodsData;
import lombok.Data;

import java.util.List;

//首页猫选择
public class TaskRewardReceiveResponse extends BaseMsg<TaskRewardReceiveResponse.ResponseData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kTaskRewardReceiveCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kTaskRewardReceiveCommand;
    }

    @Data
    public static class ResponseData {
        //已领取的奖励列表
        List<GoodsData> goodsDataList;

        //已领取的任务ID
        List<Integer> taskIdList;

        //猫蛋开出的猫ID集合
        List<Integer> catIds;
    }
}
