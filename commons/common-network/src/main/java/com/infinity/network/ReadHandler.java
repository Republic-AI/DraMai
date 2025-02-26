package com.infinity.network;

import com.infinity.network.codec.ICodec;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
public class ReadHandler implements CompletionHandler<Integer, IChannel> {
    private ByteBuffer buffer_;
    private final ICodec codec_;
    private boolean reading_length_;

    ReadHandler(ICodec codec) {
        codec_ = codec;
    }

    void read(IChannel channel_) {
        if (channel_ == null || channel_.isClosed())
            return;

        AsynchronousSocketChannel socketHandler = channel_.getSocketHandler();
        if (socketHandler != null) {
            resetBuffer();
            socketHandler.read(buffer_, channel_, this);
        }
    }

    private void resetBuffer() {
        reading_length_ = true;
        buffer_ = ByteBuffer.allocate(next_len);
        buffer_.clear();
    }

    @Override
    public void completed(Integer result, IChannel channel_) {
        if (result <= 0) {
            // 如果socket已经关闭，或者已经传输完毕，需要关闭channel，改link生命周期已经结束
            log.debug("link[{},{}] is over", channel_.getID(), channel_.getNodeID());
            channel_.close();
            return;
        }

        // 更新接收时间
        channel_.updateReceiveTime();
        this.buffer_.flip();
        int receiveLength = buffer_.remaining();
        do {
            if (receiveLength < next_len) {
                buffer_.position(buffer_.limit());
                buffer_.limit(buffer_.capacity());
                break;
            }

            // 推入协议解析层
            if (reading_length_) {
                processLength(channel_);
                if (next_len <= 0 || next_len >= Constant.kMaxPacketLength) {
                    log.error("link[{},{}] next_len[{}] is invalid", channel_.getID(),
                            channel_.getNodeID(), next_len);
                    channel_.close();
                    return;
                }
            } else {
                processContent(channel_);
            }
            buffer_ = ByteBuffer.allocate(next_len);
        } while (false);
        AsynchronousSocketChannel socketHandler = channel_.getSocketHandler();
        if (socketHandler != null)
            socketHandler.read(buffer_, channel_, this);

    }

    @Override
    public void failed(Throwable exc, IChannel channel_) {
        log.error("channel {} error.msg={}", channel_.getID(), exc.getMessage());
        exc.printStackTrace();
        channel_.close();
    }

    private int next_len = Constant.kProtocolLength;

    private void processLength(IChannel channel_) {
        reading_length_ = false;
        next_len = buffer_.getInt();
    }

    private void processContent(IChannel channel_) {
        reading_length_ = true;
        next_len = Constant.kProtocolLength;
        codec_.push(buffer_, channel_); // 协议解析器
    }
}