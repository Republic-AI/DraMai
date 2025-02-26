package com.infinity.db.util;

public class ObjSerialException extends RuntimeException {
    public ObjSerialException(Throwable cause) {
        super(cause);
    }

    public ObjSerialException(String msg) {
        super(msg);
    }

    public ObjSerialException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
