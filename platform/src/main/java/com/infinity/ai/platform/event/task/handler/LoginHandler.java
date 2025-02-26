package com.infinity.ai.platform.event.task.handler;

import com.infinity.ai.platform.event.task.PlayerTask;
import com.infinity.ai.platform.event.task.enums.TaskTypeEnum;
import com.infinity.ai.domain.model.PlayerTaskEntity;
import org.springframework.util.StringUtils;

import java.util.StringJoiner;

/***
 * 3.登录游戏
 */
public class LoginHandler extends AbstractTaskHandler {

    @Override
    public TaskTypeEnum getTaskType() {
        return TaskTypeEnum.LOGIN;
    }

    /***
     * 参数格式：[0:登录日期格式：yyyyMMdd]
     * @param entity
     * @param playerTask
     * @return
     */
    @Override
    protected boolean doTask(PlayerTaskEntity entity, PlayerTask playerTask) {
        Object[] taskPrams = playerTask.getTaskPrams();
        //登录日期:yyyyMMdd
        String inValue1 = String.valueOf(taskPrams[0]);

        int value1 = getValue1(entity);
        if (StringUtils.isEmpty(entity.getValue2())) {
            value1++;
            entity.setValue1(value1);
            entity.setValue2(inValue1);
        } else if (entity.getValue2().indexOf(inValue1) < 0) {
            value1++;
            entity.setValue1(value1);
            StringJoiner joiner = new StringJoiner(",");
            entity.setValue2(joiner.add(entity.getValue2()).add(inValue1).toString());
        }

        return value1 >= playerTask.getTaskConfig().getValue1();
    }

    @Override
    protected boolean doReceive(PlayerTaskEntity entity, PlayerTask playerTask) {
        return true;
    }

}
