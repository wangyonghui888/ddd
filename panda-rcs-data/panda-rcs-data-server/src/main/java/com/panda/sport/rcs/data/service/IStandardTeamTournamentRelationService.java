package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardTeamTournamentRelation;

/**
 * <p>
 * 球队联赛关系表  
一支球队参加某联赛,该表中存且仅存放一条信息。
如果 意大利队,参加 200 服务类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
public interface IStandardTeamTournamentRelationService extends IService<StandardTeamTournamentRelation> {

}
