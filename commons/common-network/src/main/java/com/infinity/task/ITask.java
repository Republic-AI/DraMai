package com.infinity.task;

import com.infinity.network.IChannel;
import com.infinity.protocol.HeaderOuterClass;

public interface ITask {
    static final long DEFAULT_THREAD_MARK = -1L;

    int getCommandID();

    void setChannel(IChannel socketChannel);

    void setHeader(HeaderOuterClass.Header header);

    void setExtras(byte[] buffer);

    IChannel getChannel();

    HeaderOuterClass.Header getHeader();

    byte[] getExtras();

    void init();

    void execute();

    default String getTaskName() {
        return "?";
    }

    long getCreateTm();

    default long getThreadMark() {
        return DEFAULT_THREAD_MARK;
    }

    default void setAttachment(Object msg) {

    }

    default Object getAttachement() {
        return null;
    }

}