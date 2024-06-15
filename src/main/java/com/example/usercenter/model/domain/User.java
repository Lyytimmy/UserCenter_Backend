package com.example.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 登录账户
     */
    private String userAccount;

    /**
     * 头像url
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态 0正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除 0否 1是
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 角色
     * 0-普通用户 1-管理员
     */
    private Integer role;

    /**
     * 标签列表json
     */
    private String tags;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
