package com.infinity.network;

import com.infinity.register.IRegisterData;
import com.infinity.register.RedisRegister;
import com.infinity.task.ITaskFactory;
import com.infinity.task.ITaskManager;

public class ManagerService {
    private static ChannelManager channel_manager_ = null;
    private static ChannelIDManager channel_id_manager_ = null;
    private static ITaskFactory task_factory_ = null;
    private static ITaskManager task_manager_ = null;
    private static IRegisterData register = null;

    public static void init() {
        channel_manager_ = new ChannelManager();
        channel_id_manager_ = new ChannelIDManager();
        register = new RedisRegister();
    }

    public static void fini() {
        if (task_factory_ != null)
            task_factory_.dispose();


        channel_id_manager_ = null;
        channel_manager_ = null;
        task_manager_ = null;
        task_factory_ = null;
        register = null;
    }

    static ChannelManager getChannelManager() {
        return channel_manager_;
    }

    public static ChannelIDManager getChannelIDManager() {
        return channel_id_manager_;
    }

    public static void setTaskFactory(ITaskFactory ITaskFactory) {
        task_factory_ = ITaskFactory;
    }

    public static void setTaskManager(ITaskManager taskManager) {
        task_manager_ = taskManager;
    }

    public static ITaskFactory getTaskFactory() {
        return task_factory_;
    }

    public static ITaskManager getTaskManager() {
        return task_manager_;
    }

    public static IRegisterData getRegister() {
        return register;
    }
}