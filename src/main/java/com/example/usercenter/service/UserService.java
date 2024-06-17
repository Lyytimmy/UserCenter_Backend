package com.example.usercenter.service;

import com.example.usercenter.model.domain.Tag;
import com.example.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import static com.example.usercenter.contant.UserContant.ADMIN_ROLE;
import static com.example.usercenter.contant.UserContant.USER_LOGIN_STATE;

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

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 用户信息
     */
    User doLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户信息脱敏
     * @param originuser 用户信息
     * @return 脱敏后信息
     */
    User getSafetyUser(User originuser);

    /**
     * 用户注销
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据tags搜索用户-and模式-内存
     *
     * @param tagList
     * @return
     */
    List<User> serchUserByTags(List<String> tagList);

    /**
     * 根据tags搜索用户-and模式-sql
     * @param tagList
     * @return
     */
    List<User> serchUserByTagsBySql(List<String> tagList);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取当前用户信息
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request 请求
     * @return 是否为管理员
     */
    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);
}
