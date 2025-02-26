package com.infinity.network;

import java.nio.channels.AsynchronousSocketChannel;

public class SocketCommon {
    public static void close(AsynchronousSocketChannel socketHandle) {
        if (socketHandle == null)
            return;

        try {
            socketHandle.close();
        } catch (Exception except) {
            except.printStackTrace();
        }
    }
}