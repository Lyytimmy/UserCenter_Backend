package com.example.usercenter.job;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.usercenter.mapper.UserMapper;
import com.example.usercenter.model.domain.User;
import com.example.usercenter.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 预热缓存
 */
@Slf4j
@Component
public class PreCacheJob {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private UserService userService;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    /**
     * 每天执行
     * 预热推荐用户
     */
    @Scheduled(cron = "0 58 23 * * ?")
    public void preCacheRecommendUser(){
        log.info("开始预热推荐用户缓存");
        for (Long userId : mainUserList){
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            Page<User> userList = userService.page(new Page<>(1,1), queryWrapper);
            String redisKey = String.format("usercenter:user:recommend:%s",userId);
            ValueOperations<String,Object> operations = redisTemplate.opsForValue();
            try {
                operations.set(redisKey,userList,24, TimeUnit.HOURS);
            } catch (Exception e){
                log.error("redis set key error",e);
            }
        }
        log.info("结束预热推荐用户缓存");
    }
}
