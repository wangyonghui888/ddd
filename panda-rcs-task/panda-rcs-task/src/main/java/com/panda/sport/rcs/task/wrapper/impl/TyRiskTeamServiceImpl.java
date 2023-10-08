package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsDangerTeamMapper;
import com.panda.sport.rcs.pojo.danger.RcsDangerTeam;
import com.panda.sport.rcs.task.wrapper.TyRiskTeamService;
import org.springframework.stereotype.Service;

@Service
public class TyRiskTeamServiceImpl extends ServiceImpl<RcsDangerTeamMapper, RcsDangerTeam> implements TyRiskTeamService{


}
