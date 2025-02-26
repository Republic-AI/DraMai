package com.infinity.common.base.exception;

import lombok.Data;

@Data
public class Result<T> {

    private ResultCode resultCode;

    private T data;

    public static Result ok() {
        return new Result(ResultCode.SUCCESS);
    }

    public Result(ResultCode resultCode) {
        this.resultCode = resultCode;
    }
}
