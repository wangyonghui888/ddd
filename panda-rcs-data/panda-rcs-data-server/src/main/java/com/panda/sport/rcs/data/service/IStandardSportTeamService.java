package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportTeam;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 标准球队信息表.
球队id 与比赛id 作为唯一性约束 服务类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
public interface IStandardSportTeamService extends IService<StandardSportTeam> {

    int batchInsert(List<StandardSportTeam> standardSportTeams);

    List<StandardSportTeam> listByListIds(ArrayList<Long> sportTeamListLongs);

    int batchInsertOrUpdate(List<StandardSportTeam> standardSportTeams);
}
