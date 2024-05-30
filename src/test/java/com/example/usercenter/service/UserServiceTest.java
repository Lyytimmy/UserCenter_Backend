package com.example.usercenter.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        long result = userService.userRegister("lyy211","123456789","123456789");
        Assertions.assertTrue(result > 0);
    }
}
