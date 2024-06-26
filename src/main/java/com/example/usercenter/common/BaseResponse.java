package com.example.usercenter.common;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用返回类
 *
 */
@Data
public class BaseResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 5152499787463715320L;

    private int code;
    private String message;
    private String desciption;
    private T data;

    public BaseResponse(int code, String message, String desciption, T data) {
        this.code = code;
        this.message = message;
        this.desciption = desciption;
        this.data = data;
    }

    public BaseResponse(int code, T data) {
        this(code, "","", data);
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),errorCode.getMessage(),errorCode.getDesciption(),null);
    }
}
