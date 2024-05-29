package com.example.usercenter.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter.model.domain.User;
import com.example.usercenter.mapper.UserMapper;
import com.example.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
* @author 25006
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-05-30 05:47:00
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            return -1;
        }
        if(userAccount.length()<4){
            return -1;
        }
        if (userPassword.length()<8 || checkPassword.length()<8){
            return -1;
        }
        // 校验账户不能包含特殊字符
        Pattern pattern=Pattern.compile("^[a-zA-Z0-9_]+$");
        Matcher matcher = pattern.matcher(userAccount);
        //匹配上的时候返回true,匹配不通过返回false
        if (!matcher.matches()){
            return -1;
        }
        if(!userPassword.equals(checkPassword)){
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if(count > 0){
            return -1;
        }
        // 2.密码加密
        // 加言
        final String SALT = "lyy";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        // 3.插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getId();
    }
}




