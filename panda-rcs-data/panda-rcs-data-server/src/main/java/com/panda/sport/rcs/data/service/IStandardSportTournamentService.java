package com.panda.sport.rcs.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.StandardSportTournament;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * SR中对应tournament
BC中对应Competition
 服务类
 * </p>
 *
 * @author Vector
 * @since 2019-09-26
 */
public interface IStandardSportTournamentService extends IService<StandardSportTournament> {

    Long getLastCrtTime();

    int batchInsert(List<StandardSportTournament> standardSportTournaments);

    List<StandardSportTournament> listByListIds(ArrayList<Long> sportTournamentDataLongs);

    int batchInsertOrUpdate(List<StandardSportTournament> standardSportTournaments);
}
