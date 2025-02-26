
package com.infinity.network.codec;

import com.infinity.network.IChannel;

import java.nio.ByteBuffer;

public interface ICodec {
    void push(ByteBuffer rawData, IChannel channel);

    void dispose();
}