package com.example.usercenter.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter.model.domain.User;
import com.example.usercenter.mapper.UserMapper;
import com.example.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.usercenter.contant.UserContant.USER_LOGIN_STATE;

/**
* @author 25006
* @description 针对表【user(用户表)】的数据库操作Service实现
* @createDate 2024-05-30 05:47:00
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值
     */
    private static final String SALT = "lyy";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        // todo 修改自定义异常
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

    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            return null;
        }
        if(userAccount.length()<4){
            return null;
        }
        if (userPassword.length()<8){
            return null;
        }
        // 校验账户不能包含特殊字符
        Pattern pattern=Pattern.compile("^[a-zA-Z0-9_]+$");
        Matcher matcher = pattern.matcher(userAccount);
        //匹配上的时候返回true,匹配不通过返回false
        if (!matcher.matches()){
            return null;
        }
        // 2.校验用户是否存在
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("user login failed,userAccount cannot match userPassword");
            return null;
        }
        // 3.用户信息脱敏
        User safetyUser = getSafetyUser(user);
        // 4.记录用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    @Override
    public User getSafetyUser(User originuser){
        User safetyUser = new User();
        safetyUser.setId(originuser.getId());
        safetyUser.setUsername(originuser.getUsername());
        safetyUser.setUserAccount(originuser.getUserAccount());
        safetyUser.setAvatarUrl(originuser.getAvatarUrl());
        safetyUser.setGender(originuser.getGender());
        safetyUser.setPhone(originuser.getPhone());
        safetyUser.setEmail(originuser.getEmail());
        safetyUser.setUserStatus(originuser.getUserStatus());
        safetyUser.setCreateTime(originuser.getCreateTime());
        safetyUser.setRole(originuser.getRole());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
}




