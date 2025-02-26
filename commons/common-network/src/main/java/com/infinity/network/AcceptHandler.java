package com.infinity.network;

import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Acceptor> {
    private ChannelOption channel_option_ = null;

    public AcceptHandler(ChannelOption acceptOption) {
        channel_option_ = acceptOption;
    }

    @Override
    public void completed(final AsynchronousSocketChannel newChannel, Acceptor acceptor) {
        try {

            Channel acceptChannel = new Channel(newChannel, channel_option_);
            log.info("new channel[{}],lAddr:{},rAddr:{}", acceptChannel.getID(),
                    newChannel.getLocalAddress().toString(),
                    newChannel.getRemoteAddress().toString());
            acceptChannel.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 监听新的请求，递归调用。
            acceptor.accept(acceptor, this);
        }
    }

    @Override
    public void failed(Throwable exc, Acceptor acceptor) {
        try {
            log.error(exc.getMessage());
            exc.printStackTrace();
        } finally {
            AsynchronousCloseException exception = (AsynchronousCloseException) exc;
            if (exception != null)
                return;

            // 监听新的请求，递归调用。
            acceptor.accept(acceptor, this);
        }
    }
}