package com.infinity.task;

import com.infinity.network.IChannel;
import com.infinity.protocol.HeaderOuterClass.Header;

public interface ITaskFactory {
    void dispose();

    ITask createTask(int command, Header header, byte[] buffer, IChannel channel, Object attachement);
}