package com.example.usercenter.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter.common.ErrorCode;
import com.example.usercenter.exception.BusinessException;
import com.example.usercenter.model.domain.Tag;
import com.example.usercenter.model.domain.User;
import com.example.usercenter.mapper.UserMapper;
import com.example.usercenter.service.UserService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号小于四位");
        }
        if (userPassword.length()<8 || checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码小于八位");
        }
        // 校验账户不能包含特殊字符
        Pattern pattern=Pattern.compile("^[a-zA-Z0-9_]+$");
        Matcher matcher = pattern.matcher(userAccount);
        //匹配上的时候返回true,匹配不通过返回false
        if (!matcher.matches()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次密码不一致");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账户已注册");
        }
        // 2.密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        // 3.插入数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult){
            throw new BusinessException(ErrorCode.NULL_ERROR,"插入数据库失败");
        }
        return user.getId();
    }

    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if(userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号小于四位");
        }
        if (userPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码小于八位");
        }
        // 校验账户不能包含特殊字符
        Pattern pattern=Pattern.compile("^[a-zA-Z0-9_]+$");
        Matcher matcher = pattern.matcher(userAccount);
        //匹配上的时候返回true,匹配不通过返回false
        if (!matcher.matches()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        // 2.校验用户是否存在
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("user login failed,userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.NULL_ERROR,"为查询到用户");
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
        safetyUser.setTags(originuser.getTags());
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    @Override
    public List<User> serchUserByTags(List<String> tagList) {
        if(CollectionUtils.isEmpty(tagList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在mysql中查询
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        for (String tagName:tagList){
//            queryWrapper = queryWrapper.like("tags",tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);

        // 在内存中查询
        // 先查所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 在内存中判断是否包含要求的标签
        return userList.stream().filter(user->{
            String tagsStr = user.getTags();
            if (StringUtils.isBlank(tagsStr)){
                return false;
            }
            Set<String> tempTagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>(){}.getType());
            for (String tagName : tagList) {
                if (!tempTagNameSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
        // 脱敏
        // return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }
}




