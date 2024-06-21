package com.example.usercenter.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.usercenter.common.ErrorCode;
import com.example.usercenter.exception.BusinessException;
import com.example.usercenter.model.Enum.TeamStatusEnum;
import com.example.usercenter.model.domain.Team;
import com.example.usercenter.model.domain.User;
import com.example.usercenter.model.domain.UserTeam;
import com.example.usercenter.model.dto.TeamQuery;
import com.example.usercenter.model.request.TeamUpdateRequest;
import com.example.usercenter.model.vo.TeamUserVO;
import com.example.usercenter.model.vo.UserVO;
import com.example.usercenter.service.TeamService;
import com.example.usercenter.mapper.TeamMapper;
import com.example.usercenter.service.UserService;
import com.example.usercenter.service.UserTeamService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.example.usercenter.contant.SystemContant.SALT;

/**
* @author 25006
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-06-20 19:37:33
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;

    @Override
    public Team getSafetyTeam(Team originteam) {
        Team newTeam = new Team();
        newTeam.setId(originteam.getId());
        newTeam.setName(originteam.getName());
        newTeam.setDescription(originteam.getDescription());
        newTeam.setMaxNum(originteam.getMaxNum());
        newTeam.setExpireTime(originteam.getExpireTime());
        newTeam.setUserId(originteam.getUserId());
        newTeam.setStatus(originteam.getStatus());
        newTeam.setCreateTime(originteam.getCreateTime());
        return newTeam;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        // 1. 校验是否为空
        if (team==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 是否登录
        if (loginUser==null){
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        final long userId = loginUser.getId();
        // 3. 校验信息
        // 1<队伍人数<=20
        int MaxNUm = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (MaxNUm<=1 || MaxNUm>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不合法");
        }
        // 队伍名称小于20个字
        String name = team.getName();
        if (StringUtils.isBlank(name) && name.length()>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名称不合法");
        }
        // 描述小于等于512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) || description.length()>512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述不合法");
        }
        // staute是否公开
        int statue = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(statue);
        if (teamStatusEnum==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不合法");
        }
        // 如果是私密队伍，需要设置密码，密码长度小于等于32
        if (teamStatusEnum==TeamStatusEnum.PRIVATE){
            String password = team.getPassword();
            if (StringUtils.isBlank(password) || password.length()>32){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍密码不合法");
            }
        }
        // 超时时间 > 当前时间
        if (Calendar.getInstance().getTime().after(team.getExpireTime())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍超时时间不合法");
        }
        // 校验用户最多创建5个用户，链表查询
        // 分布式锁，防止用户短时间内点击多次创建多个队伍
        String lockName = "usercenter:TeamServiceImpl:addTeam:userId:"+userId;
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(0,-1, TimeUnit.SECONDS)){
                QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("userId",loginUser.getId());
                long hasTeamNumber = count(queryWrapper);
                if (hasTeamNumber>=5){
                    throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户创建队伍数量超过限制");
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
        // 插入队伍表
        // 加密队伍密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT+team.getPassword()).getBytes());
        team.setPassword(encryptPassword);
        team.setId(null);
        team.setUserId(userId);
        boolean save = save(team);
        Long teamId = team.getId();
        if (!save || teamId==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"插入失败");
        }

        // 插入队伍用户表 这里要放在一个事务里
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        save = userTeamService.save(userTeam);
        if (!save) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        // 1. 从请求参数中取数据，如果没有参数就是查所有
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 组合查询条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtils.isNotEmpty(idList)) {
                queryWrapper.in("id", idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            // 查询最大人数相等的
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            // 根据创建人来查询
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            // 根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        // 不展示已过期的队伍
        // expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            // 脱敏用户信息
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 只有管理员或者队伍的创建者可以修改
        if (oldTeam.getUserId() != loginUser.getId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须要设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }
}




