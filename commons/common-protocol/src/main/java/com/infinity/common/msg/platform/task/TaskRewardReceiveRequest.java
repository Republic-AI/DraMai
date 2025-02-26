package com.infinity.common.msg.platform.task;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;

//任务奖励领取
public class TaskRewardReceiveRequest extends BaseMsg<TaskRewardReceiveRequest.RequestData> {

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
    public static class RequestData {
        //任务ID，对应配置表的ID
        private int taskId;
    }
}
