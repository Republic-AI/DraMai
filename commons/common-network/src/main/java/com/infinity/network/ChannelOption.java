package com.infinity.network;

public class ChannelOption {
    public final static int kDefaultSize = 1024*1024;
    protected boolean is_no_delay_ = true;
    protected int send_buffer_size_ = kDefaultSize;
    protected int receive_buffer_size_ = kDefaultSize;

    public ChannelOption() {
    }

    public boolean isNoDelay() {
        return is_no_delay_;
    }

    public int getSendBufferSize() {
        return send_buffer_size_;
    }

    public int getReceiveBufferSize() {
        return receive_buffer_size_;
    }
}