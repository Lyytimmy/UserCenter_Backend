package com.example.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.usercenter.common.BaseResponse;
import com.example.usercenter.common.ErrorCode;
import com.example.usercenter.exception.BusinessException;
import com.example.usercenter.model.domain.Team;
import com.example.usercenter.model.domain.User;
import com.example.usercenter.model.dto.TeamAddRequest;
import com.example.usercenter.model.dto.TeamQuery;
import com.example.usercenter.service.TeamService;
import com.example.usercenter.service.UserService;
import com.example.usercenter.utils.ResultUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jodd.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static com.example.usercenter.contant.SystemContant.SALT;

/**
 * 队伍接口
 */
@RestController
@Slf4j
@RequestMapping("/team")
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team,teamAddRequest);
        } catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        long teamId = teamService.addTeam(team, loginUser);
        if (teamId<=0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"创建失败");
        }
        return ResultUtils.success(teamId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean remove = teamService.removeById(id);
        if (!remove){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("update")
    public BaseResponse<Boolean> updateTeam(@RequestBody Team team){
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean update = teamService.updateById(team);
        if (!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"查询失败");
        }
        // 脱敏
        Team safeTeam = teamService.getSafetyTeam(team);
        return ResultUtils.success(safeTeam);
    }

    @GetMapping("/list")
    public BaseResponse<Page<Team>> lisTeams(TeamQuery teamQuery){
        if (teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team,teamQuery);
        } catch (Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        Page<Team> page = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> res = teamService.page(page, queryWrapper);
        return ResultUtils.success(res);
    }

}
