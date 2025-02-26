package com.infinity.db.except;

public class DBServiceException extends RuntimeException {
    public DBServiceException(String msg) {
        super(msg);
    }
}