package com.example.usercenter.service;

import com.example.usercenter.model.domain.User;
import com.example.usercenter.utils.ObjectUtils;
import jakarta.annotation.Resource;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        long result = userService.userRegister("lyy211","123456789","123456789");
        Assertions.assertTrue(result > 0);
    }
    @Test
    void testObjectUtils(){
        User user = new User();
        List<String> excludeFields = Arrays.asList("id","serialVersionUID");
        boolean res = ObjectUtils.objectCheckIsNull(user,excludeFields);
        Assert.assertTrue(res);
    }
}
