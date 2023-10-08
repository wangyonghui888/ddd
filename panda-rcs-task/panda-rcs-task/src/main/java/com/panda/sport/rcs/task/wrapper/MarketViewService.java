package com.panda.sport.rcs.task.wrapper;

import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;

public interface MarketViewService {

    /**
     * 更新同联赛赛事实货量
     *
     * @param matchDimensionStatistics
     */
    boolean updateMatchBetChange(RcsMatchDimensionStatistics matchDimensionStatistics);

    /**
     * 联赛正在开售滚球的有几场
     * @param standardTournamentId
     * @return
     */
    Long getRollNum(Long standardTournamentId);


    Long getOtherCategoryNum(Long matchId);


}
