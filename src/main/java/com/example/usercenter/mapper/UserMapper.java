package com.example.usercenter.mapper;

import com.example.usercenter.model.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 25006
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2024-05-30 05:47:00
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




