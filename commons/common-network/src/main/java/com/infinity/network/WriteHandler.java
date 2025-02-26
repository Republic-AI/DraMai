package com.infinity.network;

import lombok.extern.slf4j.Slf4j;

import java.nio.channels.CompletionHandler;

@Slf4j
public class WriteHandler implements CompletionHandler<Integer, IChannel> {

    @Override
    public void completed(Integer result, IChannel channel) {
        if (channel != null) {
            channel.popWriteDone();
        }
    }

    @Override
    public void failed(Throwable exc, IChannel channel) {
        log.error("channel {} error.msg={}", channel.getID(), exc.getMessage());
        exc.printStackTrace();
        channel.close();
    }
}