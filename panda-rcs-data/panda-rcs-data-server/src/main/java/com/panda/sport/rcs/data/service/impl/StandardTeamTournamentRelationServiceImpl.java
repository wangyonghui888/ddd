package com.panda.sport.rcs.data.service.impl;

import com.panda.sport.rcs.data.mapper.StandardTeamTournamentRelationMapper;
import com.panda.sport.rcs.data.service.IStandardTeamTournamentRelationService;
import com.panda.sport.rcs.pojo.StandardTeamTournamentRelation;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 球队联赛关系表  
一支球队参加某联赛,该表中存且仅存放一条信息。
如果 意大利队,参加 200 服务实现类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
@Service
public class StandardTeamTournamentRelationServiceImpl extends ServiceImpl<StandardTeamTournamentRelationMapper, StandardTeamTournamentRelation> implements IStandardTeamTournamentRelationService {

}
