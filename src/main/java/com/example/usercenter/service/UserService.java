package com.example.usercenter.service;

import com.example.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 25006
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2024-05-30 05:47:00
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 用户校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

}
