package com.infinity.common.msg.platform.task;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import lombok.Data;


/**
 * 服务端推送玩家任务重置命令给客户端
 * 每周一早上5点周任务重置通知，子命令号 69
 * 每天早上5点日常任务重置通知，子命令号 70
 * 源节点: 平台管理节点
 * 目标节点: 客户端节点
 * 回应只能是: response_ok
 **/
public class NotifyTaskResetRequest extends BaseMsg<NotifyTaskResetRequest.RequestData> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kTaskRestNotifyCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kTaskRestNotifyCommand;
    }

    @Data
    public static class RequestData {
        //1:重置日任务，2：重置周任务
        private Integer type;
    }
}
