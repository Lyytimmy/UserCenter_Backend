package com.example.usercenter.utils;


import com.example.usercenter.common.BaseResponse;
import com.example.usercenter.common.ErrorCode;

/**
 * 返回工具类
 */
public class ResultUtils {
    // 成功
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, "OK", "",data);
    }

    // 失败
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode.getCode(),errorCode.getMessage(),errorCode.getDesciption(),null);
    }

    public static BaseResponse error(int code, String message,String description){
        return new BaseResponse<>(code,message,description,null);
    }

    public static BaseResponse error(ErrorCode errorCode, String message,String description){
        return new BaseResponse<>(errorCode.getCode(),message,description,null);
    }
}
