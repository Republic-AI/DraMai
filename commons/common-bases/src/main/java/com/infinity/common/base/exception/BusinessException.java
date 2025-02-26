package com.infinity.common.base.exception;


import lombok.Getter;

/**
 * 业务异常
 */
@Getter
public class BusinessException extends RuntimeException {
    private int code;
    private Object data;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAILURE.getCode();
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(Integer code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public BusinessException(ResultCode code) {
        super(code.getMessage());
        this.code = code.getCode();
    }

    public BusinessException(ResultCode code, Object data) {
        super(code.getMessage());
        this.code = code.getCode();
        this.data = data;
    }
}