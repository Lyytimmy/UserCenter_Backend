package com.example.usercenter;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.example.usercenter.mapper.UserMapper;
import com.example.usercenter.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SampleTest {
    @Resource
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        // 断言预期值与实际值相等
        Assert.isTrue(5 == userList.size(), "");
        userList.forEach(System.out::println);
    }
}
