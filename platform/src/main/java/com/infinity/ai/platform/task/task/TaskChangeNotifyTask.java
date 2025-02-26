package com.infinity.ai.platform.task.task;

import com.infinity.common.msg.ProtocolCommon;
import com.infinity.common.msg.platform.task.NotifyTaskRequest;
import com.infinity.common.msg.platform.npc.TaskData;
import com.infinity.manager.task.BaseTask;
import com.infinity.network.RequestIDManager;
import com.infinity.task.IRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/***
 * 任务数据变动通知
 */
public class TaskChangeNotifyTask extends BaseTask implements IRequest {

    private final long playerId;
    private final List<NotifyData> dataList;
    private final long requestId;

    public TaskChangeNotifyTask(final Long playerId, List<NotifyData> dataList) {
        this.playerId = playerId;
        this.dataList = dataList;
        requestId = RequestIDManager.getInstance().RequestID(false);
    }

    @Override
    public long getThreadMark() {
        return playerId;
    }

    @Override
    public long getRequestID() {
        return requestId;
    }

    @Override
    public int getCommandID() {
        return ProtocolCommon.kTaskChangeNotifyCommand;
    }

    @Override
    public void init() {
    }

    @Override
    public boolean run0() {
        sendMsg();
        RequestIDManager.getInstance().removeResponse(requestId);
        return true;
    }

    private void sendMsg() {
        if (dataList.size() == 0) {
            return;
        }

        List<TaskData> taskList = new ArrayList<>();
        this.dataList.forEach(d -> {
            TaskData data = new TaskData();
            data.setTaskId(d.getTaskId());
            data.setStatus(d.getStatus());
            data.setValue1(d.getValue());
            taskList.add(data);
        });

        NotifyTaskRequest response = new NotifyTaskRequest();
        response.setRequestId(requestId);
        response.setSessionId(null);
        response.setPlayerId(playerId);
        response.setData(taskList);

        this.sendMessage(playerId, response);
    }

    @Setter
    @Getter
    public static class NotifyData {
        private Integer taskId; //任务ID
        private Integer status; //状态
        private Integer value; //值
    }
}
