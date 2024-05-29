package com.example.usercenter.service;

import com.example.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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
