package com.infinity.ai.platform.event.task.handler;

import com.infinity.ai.domain.model.PlayerTaskEntity;
import com.infinity.ai.platform.event.task.PlayerTask;
import com.infinity.ai.platform.event.task.enums.TaskTypeEnum;
import org.springframework.util.ObjectUtils;

import java.util.StringJoiner;

/***
 * 100.根据完成的任务ID
 */
public class FnishTaskHandler extends AbstractTaskHandler {

    @Override
    public TaskTypeEnum getTaskType() {
        return TaskTypeEnum.BY_FINISH;
    }

    @Override
    protected boolean doTask(PlayerTaskEntity entity, PlayerTask playerTask) {
        Object[] taskPrams = playerTask.getTaskPrams();
        String inValue1 = String.valueOf(taskPrams[0]);
        int value1 = this.getValue1(entity);
        if (playerTask.getTaskConfig().getValue2().contains(Integer.parseInt(inValue1))) {
            if (ObjectUtils.isEmpty(entity.getValue2()) || ("," + entity.getValue2() + ",").indexOf("," + inValue1 + ",") < 0) {
                value1++;
                StringJoiner joiner = new StringJoiner(",");
                if (ObjectUtils.isEmpty(entity.getValue2())) {
                    entity.setValue2(joiner.add(inValue1).toString());
                } else {
                    entity.setValue2(joiner.add(entity.getValue2()).add(inValue1).toString());
                }

                entity.setValue1(value1);
            }
        }

        return value1 == playerTask.getTaskConfig().getValue1();
    }

    @Override
    protected boolean doReceive(PlayerTaskEntity entity, PlayerTask playerTask) {
        return true;
    }

}
