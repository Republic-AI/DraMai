package com.infinity.ai.platform.event.event;

import com.infinity.ai.platform.event.ActionParams;

//根据完成的任务ID
public class TaskFinishAction extends AbstractAction {

    /**
     * taskPrams 格式: [0:已完成的任务ID]
     * @param params
     */
    @Override
    public void execute(ActionParams params) {
        try {
            long playerId = params.getPlayerId();
            String inParams = String.valueOf(params.getTaskPrams()[0]);

            /*//根据完成的任务ID
            PlayerTaskManager.getInstance().doTask(playerId, TaskTypeEnum.TASK13, inParams);

            //20.累计完成日常任务XX次
            //21.累计完成周常XX次
            TaskCfg config = GameConfigManager.getInstance().getTaskDataManager().getConfigWithID(Integer.parseInt(inParams));
            if (config != null && isDailyTask(config.getPosition())) {
                if (isDayTask(config.getMin_position())) {
                    //2日常
                    PlayerTaskManager.getInstance().doTask(playerId, TaskTypeEnum.TASK20, 1);
                } else if (isWeekTask(config.getMin_position())) {
                    //3周常
                    PlayerTaskManager.getInstance().doTask(playerId, TaskTypeEnum.TASK21, 1);
                }
            }

            //按完成解锁任务
            int unlockValue = Integer.parseInt(inParams);
            TaskCfg taskConfig = new TaskCfg();
            taskConfig.setUnlock(UnlockTypeEnum.FINISH.getCode());
            taskConfig.setUnlockvalue(unlockValue);
            PlayerTaskContext.getUnlock(UnlockTypeEnum.FINISH.getCode()).unlock(playerId, unlockValue, taskConfig);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //任务类型:1:日常任务,2:成就任务,3:竞技任务
    private boolean isDailyTask(Integer taskType) {
        return taskType != null && taskType == 1;
    }

    //任务子类型:日常任务（1主线、2日常、3周常、4支线、5日常活跃、6周常活跃 7主线章节),
    private boolean isDayTask(Integer position) {
        return position != null && position == 2;
    }

    //任务子类型:日常任务（1主线、2日常、3周常、4支线、5日常活跃、6周常活跃 7主线章节),
    private boolean isWeekTask(Integer position) {
        return position != null && position == 3;
    }

}
