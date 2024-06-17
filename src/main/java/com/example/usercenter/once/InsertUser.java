package com.example.usercenter.once;

import com.example.usercenter.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class InsertUser {
    @Resource
    private UserMapper userMapper;

    /**
     * 导入用户数据任务
     */
    @Scheduled
    public void doInsertUser() {

    }
}
