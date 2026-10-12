package com.lzj.exception;

import com.lzj.result.ResultCode;
import lombok.Getter;

/** @author 李兆基 */
@Getter
public class ServiceException extends RuntimeException {

    private final ResultCode resultCode;

    public ServiceException(ResultCode resultCode, String coderMessage) {
        super(coderMessage);
        this.resultCode = resultCode;
    }
}
