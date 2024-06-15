package com.example.usercenter.service;

import com.example.usercenter.model.domain.User;
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
    void serchUserByTags() {
        List<String> taglist = Arrays.asList("java","python");
        List<User> userList = userService.serchUserByTags(taglist);
        Assert.assertNotNull(userList);
    }
}
