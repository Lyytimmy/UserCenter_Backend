package com.example.usercenter.model.request;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 */
@Data
public class UserRegisterRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -150010351605082849L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
