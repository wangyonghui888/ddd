package com.panda.sport.rcs.mgr.wrapper;

import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.vo.operation.RealTimeVolumeBean;

public interface MarketViewService {

    /**
     * 更新实货量、期望值
     *
     * @param dataVo
     */
    boolean updateMatchOdds(RealTimeVolumeBean dataVo);

    /**
     * 更新同联赛赛事实货量
     *
     * @param matchDimensionStatistics
     */
    boolean updateMatchBetChange(RcsMatchDimensionStatistics matchDimensionStatistics);


}
