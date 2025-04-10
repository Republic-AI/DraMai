package com.infinity.ai.platform.task.system;


import com.infinity.network.ManagerService;
import com.infinity.task.ITask;

import java.util.List;

/**
 * 广播消息
 */
public class BroadcastMesage {

    /**
     * 广播消息
     *
     * @param playerId 玩家ID
     * @param message  广播的内容
     */
    public void send(long playerId, String message) {
        ITask task = new BroadcastTask(playerId, message);
        task.init();
        ManagerService.getTaskManager().add(task);
    }



    private static class Holder {
        private static final BroadcastMesage K_INSTANCE = new BroadcastMesage();
    }

    public static BroadcastMesage getInstance() {
        return Holder.K_INSTANCE;
    }
}


