package com.infinity.common.msg.platform.task;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.TaskData;

import java.util.List;

//查询任务信息
public class QueryTaskResponse extends BaseMsg<List<TaskData>> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kQueryTaskCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kQueryTaskCommand;
    }
}
