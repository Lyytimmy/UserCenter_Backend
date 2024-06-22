package com.example.usercenter.service;

import com.example.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.usercenter.model.domain.User;
import com.example.usercenter.model.dto.TeamQuery;
import com.example.usercenter.model.request.TeamJoinRequest;
import com.example.usercenter.model.request.TeamQuitRequest;
import com.example.usercenter.model.request.TeamUpdateRequest;
import com.example.usercenter.model.vo.TeamUserVO;

import java.util.List;

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

    /**
     * 查询队伍信息
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 加入队伍
     *
     * @param teamJoinRequest
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 退出队伍
     *
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 删除（解散）队伍
     *
     * @param id
     * @param loginUser
     * @return
     */
    boolean deleteTeam(long id, User loginUser);


}
