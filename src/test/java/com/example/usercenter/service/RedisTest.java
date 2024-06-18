package com.example.usercenter.service;

import jakarta.annotation.Resource;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;
    @Test
    public void test() {
        // 获得一个redis的操作对象
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 增 key - value
        valueOperations.set("test", "mytest");
        // 查 key
        String res = (String) valueOperations.get("test");
        Assert.assertTrue(res.equals("mytest"));
        // 改 value
        valueOperations.getAndSet("test", "mytest2");
        res = (String) valueOperations.get("test");
        Assert.assertTrue(res.equals("mytest2"));
        // 删
        redisTemplate.delete("test");
        Assert.assertTrue(redisTemplate.hasKey("test") == false);
    }
}
