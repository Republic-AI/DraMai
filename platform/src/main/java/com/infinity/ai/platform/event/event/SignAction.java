package com.infinity.ai.platform.event.event;

import com.infinity.ai.platform.event.ActionParams;
import com.infinity.ai.platform.manager.PlayerTaskManager;
import com.infinity.ai.platform.event.task.enums.TaskTypeEnum;

import java.text.SimpleDateFormat;
import java.util.Date;

//签到
public class SignAction extends AbstractAction {

    /**
     * taskPrams 格式: []
     *
     * @param params
     */
    @Override
    public void execute(ActionParams params) {
        try {
            long playerId = params.getPlayerId();

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String DateStr = format.format(new Date());
            PlayerTaskManager.getInstance().doTask(playerId, TaskTypeEnum.LOGIN, DateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
