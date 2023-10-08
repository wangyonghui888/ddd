package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.mapper.RcsDangerTournamentMapper;
import com.panda.sport.rcs.pojo.danger.RcsDangerTournament;
import com.panda.sport.rcs.task.wrapper.TyRiskTournamentService;
import org.springframework.stereotype.Service;

@Service
public class TyRiskTournamentServiceImpl extends ServiceImpl<RcsDangerTournamentMapper, RcsDangerTournament> implements TyRiskTournamentService {
}
