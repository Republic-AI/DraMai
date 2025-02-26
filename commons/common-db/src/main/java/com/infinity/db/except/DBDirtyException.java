package com.infinity.db.except;

public class DBDirtyException extends Exception {
    public DBDirtyException(String msg, Throwable e) {
        super(msg, e);
    }
}