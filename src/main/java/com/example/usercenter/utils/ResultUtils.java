package com.example.usercenter.utils;


import com.example.usercenter.common.BaseResponse;

/**
 * 返回工具类
 */
public class ResultUtils {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, "OK", data);
    }

    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(0, "OK", null);
    }

    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, message, null);
    }

    public static <T> BaseResponse<T> error(int code) {
        return new BaseResponse<>(code, "", null);
    }
}
