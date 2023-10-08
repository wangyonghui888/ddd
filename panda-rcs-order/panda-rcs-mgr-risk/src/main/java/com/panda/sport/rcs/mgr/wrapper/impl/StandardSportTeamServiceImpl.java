package com.panda.sport.rcs.mgr.wrapper.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.StandardSportTeamMapper;
import com.panda.sport.rcs.pojo.StandardSportTeam;
import com.panda.sport.rcs.mgr.wrapper.StandardSportTeamService;
import com.panda.sport.rcs.vo.SportTeam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 标准球队信息表.
球队id 与比赛id 作为唯一性约束 服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2019-09-03
 */
@Service
public class StandardSportTeamServiceImpl extends ServiceImpl<StandardSportTeamMapper, StandardSportTeam> implements StandardSportTeamService {

    @Autowired
    private StandardSportTeamMapper sportTeamMapper;

    @Override
    public List<SportTeam> queryTeamsByMatchId(Long matchId) {
        return  sportTeamMapper.selectTeamsByMatchId(matchId);
    }
}
