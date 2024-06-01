package com.example.usercenter.exception;

import com.example.usercenter.common.ErrorCode;
import lombok.Getter;

import java.io.Serial;

/**
 * 自定义异常类
 */
@Getter
public class BusinessException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 631504623619249781L;

    private final int code;
    private final String description;

    public BusinessException(String message, String description, int code) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDesciption();
    }

    public BusinessException(ErrorCode errorCode,String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

}
