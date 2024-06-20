package com.example.usercenter.service;

import com.example.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usercenter.model.domain.User;

/**
* @author 25006
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-06-20 19:37:33
*/
public interface TeamService extends IService<Team> {

    /**
     * 队伍信息脱敏
     * @param originteam
     * @return
     */
    Team getSafetyTeam(Team originteam);

    /**
     * 创建队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, User loginUser);
}
