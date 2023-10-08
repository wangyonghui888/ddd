package com.panda.sport.rcs.task.wrapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsOrderSummary;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import com.panda.sport.rcs.vo.OrderSummaryVo;

import java.util.Collection;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.task.wrapper
 * @Description :  TODO
 * @Date: 2020-07-07 17:56
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsOrderSummaryService extends IService<RcsOrderSummary> {
    /**
     * @return void
     * @Description
     * @Param [orderSummaryVo]
     * @Author kimi
     * @Date 2020/7/7
     **/
    void insertOrUpdateOddsValueMax(Collection<OrderSummaryVo> orderSummaryVos);

    /**
     * @return void
     * @Description //TODO
     * @Param [oddsFieldsList, sportId, matchId, playId, marketId]
     * @Author kimi
     * @Date 2020/7/8
     **/
    void updateOrInsertOrOddsValueMax(List<MatchMarketLiveOddsVo.MatchMarketOddsFieldVo> oddsFieldsList, Long sportId, Long matchId, Long playId, Long marketId);
}
