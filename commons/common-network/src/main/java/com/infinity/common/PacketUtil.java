package com.infinity.common;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Slf4j
public final class PacketUtil {
    private static final int kGzipLength = 512;

    public static byte[] gzipBytes(byte[] bytes) {
        if (bytes == null)
            return null;

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        // Save raw data length
        int rawDataLen = bytes.length;
        outStream.write((rawDataLen >> 24) & 0xFF);
        outStream.write((rawDataLen >> 16) & 0xFF);
        outStream.write((rawDataLen >> 8) & 0xFF);
        outStream.write(rawDataLen & 0xFF);

        if (rawDataLen >= kGzipLength) {
            try {
                GZIPOutputStream goutStream = new GZIPOutputStream(outStream) {
                    {
                        def.setLevel(Deflater.BEST_SPEED);
                    }
                };
                goutStream.write(bytes, 0, rawDataLen);
                goutStream.close();
            } catch (IOException e) {
                log.error("gzip error, bytes len:" + rawDataLen, e);
                return null;
            }
        } else {
            outStream.write(bytes, 0, rawDataLen);
        }

        byte[] zipData = outStream.toByteArray();

        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipData;
    }

    public static byte[] ungzipBytes(byte[] bytes) {
        if (bytes == null)
            return null;

        ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
        int readByte = 0;

        // Read raw data length
        int rawDataLen = 0;
        for (int i = 0; i < 4; i++) {
            readByte = inStream.read();
            if (readByte == -1) {
                log.error("ungzipBytes error, cannot read out raw data length");
                return null;
            }
            rawDataLen <<= 8;
            rawDataLen += readByte;
        }

        byte[] rawBytes = new byte[rawDataLen];
        if (rawDataLen >= kGzipLength) {
            try {
                GZIPInputStream ginStream = new GZIPInputStream(inStream, rawDataLen);
                int readed = ginStream.read(rawBytes);
                if (readed != rawDataLen) {
                    log.error("ungzipBytes error");
                    return null;
                }
                ginStream.close();
            } catch (IOException e) {
                log.error("ungzip error, rawDataLen:" + rawDataLen, e);
                return null;
            }
        } else {
            try {
                inStream.read(rawBytes);
            } catch (IOException e) {
                log.error("inStream.read, rawDataLen:" + rawDataLen, e);
                return null;
            }
        }
        try {
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rawBytes;
    }
}
