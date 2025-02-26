package com.infinity.ai.platform.task.gm;

import lombok.Getter;

public class GmException extends RuntimeException {
    @Getter
    private boolean success;

    public GmException(boolean success, String msg) {
        super(msg);
        this.success = success;
    }

    public GmException(String msg) {
        this(false, msg);
    }
}
