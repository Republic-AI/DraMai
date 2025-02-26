package com.infinity.common.msg.platform.task;

import com.infinity.common.msg.BaseMsg;
import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.npc.TaskData;

import java.util.List;

//服务端推送玩家任务给客户端
public class NotifyTaskRequest extends BaseMsg<List<TaskData>> {

    @Override
    public int getType() {
        return ProtocolCommon.MSG_TYPE_PLATFORM;
    }

    @Override
    public int getCommand() {
        return ProtocolCommon.kTaskChangeNotifyCommand;
    }

    public static int getCmd() {
        return ProtocolCommon.kTaskChangeNotifyCommand;
    }
}
