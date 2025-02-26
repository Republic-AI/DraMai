package com.infinity.ai.platform.application;

import com.infinity.common.base.thread.ThreadConst;
import com.infinity.common.base.thread.Threads;
import com.infinity.common.base.thread.timer.IntervalTimer;
import com.infinity.common.msg.timer.EveryDayZeroMessage;
import com.infinity.common.utils.DateUtil;
import com.infinity.network.ManagerService;
import com.infinity.task.ITask;
import com.infinity.task.ITaskFactory;
import com.infinity.task.ITaskManager;


/**
 * 提供给 服务器启动/每日0点重置;
 */
public class GameStartInit {
    // 间隔一天
    private static final int INTERVAL = 24 * 3600 * 1000;

    public static void init() {
        //每日凌晨00:00:00运行
        initDayZero();
    }

    private static void initDayZero() {
        //启动定时任务，凌晨0点执行
        long delayTime = (DateUtil.nextDay() - System.currentTimeMillis() / 1000) * 1000;
        Threads.addListener(ThreadConst.TIMER_1S, "GameStartInit#dayZero", new IntervalTimer(delayTime, INTERVAL) {
            @Override
            public boolean exec0(int interval) {
                return dayZeroReset();
            }
        });
    }

    private static boolean dayZeroReset() {
        //重置任务
        ITaskFactory taskFactory = ManagerService.getTaskFactory();
        if (taskFactory != null) {
            EveryDayZeroMessage msg = new EveryDayZeroMessage();
            msg.setRequestId(999999999);
            msg.setNotifyType(EveryDayZeroMessage.NotifyType.DAY.getCode());
            msg.setSrc("EveryDayZeroMessage from platform TIMER_1S thread.");
            ITask newITask = taskFactory.createTask(msg.getCommand(), null, msg.toString().getBytes(), null, null);
            if (newITask != null) {
                newITask.init();
                ITaskManager taskManager = ManagerService.getTaskManager();
                taskManager.add(newITask);
            }
        }

        return false;
    }
}
