package com.example.usercenter.service.Imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter.model.domain.UserTeam;
import com.example.usercenter.service.UserTeamService;
import com.example.usercenter.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 25006
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2024-06-20 19:38:55
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




