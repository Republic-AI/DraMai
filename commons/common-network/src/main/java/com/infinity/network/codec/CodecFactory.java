
package com.infinity.network.codec;

public final class CodecFactory {
    public static ICodec Create() {
        return new LengthCodec();
    }
}