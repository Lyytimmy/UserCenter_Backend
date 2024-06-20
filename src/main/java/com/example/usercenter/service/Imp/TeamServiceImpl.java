package com.example.usercenter.service.Imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter.model.domain.Team;
import com.example.usercenter.service.TeamService;
import com.example.usercenter.mapper.TeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 25006
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-06-20 19:37:33
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

}




