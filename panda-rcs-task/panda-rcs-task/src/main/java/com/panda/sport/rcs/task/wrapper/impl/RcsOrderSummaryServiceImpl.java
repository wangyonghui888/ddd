package com.panda.sport.rcs.task.wrapper.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.panda.sport.rcs.constants.BaseConstants;
import com.panda.sport.rcs.mapper.RcsOrderSummaryMapper;
import com.panda.sport.rcs.pojo.RcsOrderSummary;
import com.panda.sport.rcs.task.wrapper.RcsOrderSummaryService;
import com.panda.sport.rcs.vo.MatchMarketLiveOddsVo;
import com.panda.sport.rcs.vo.OrderSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.task.wrapper.impl
 * @Description :  TODO
 * @Date: 2020-07-07 17:59
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Service
public class RcsOrderSummaryServiceImpl extends ServiceImpl<RcsOrderSummaryMapper, RcsOrderSummary> implements RcsOrderSummaryService {
    @Autowired
    private RcsOrderSummaryMapper rcsOrderSummaryMapper;

    /**
     * @return void
     * @Description
     * @Param [orderSummaryVo]
     * @Author kimi
     * @Date 2020/7/7
     **/
    @Override
    public void insertOrUpdateOddsValueMax(Collection<OrderSummaryVo> orderSummaryVos) {
        rcsOrderSummaryMapper.insertOrUpdateOddsValueMax(orderSummaryVos);
    }

    @Override
    public void updateOrInsertOrOddsValueMax(List<MatchMarketLiveOddsVo.MatchMarketOddsFieldVo> oddsFieldsList, Long sportId, Long matchId, Long playId, Long marketId) {
        List<RcsOrderSummary> rcsOrderSummaries = new ArrayList<>();
        for (MatchMarketLiveOddsVo.MatchMarketOddsFieldVo matchMarketOddsFieldVo : oddsFieldsList) {
            RcsOrderSummary rcsOrderSummary = new RcsOrderSummary();
            rcsOrderSummary.setOddsId(matchMarketOddsFieldVo.getId());
            rcsOrderSummary.setOddsValueMax(new BigDecimal(matchMarketOddsFieldVo.getFieldOddsValue()).divide(new BigDecimal(BaseConstants.MULTIPLE_VALUE), 2, BigDecimal.ROUND_HALF_UP));
            rcsOrderSummaries.add(rcsOrderSummary);
        }
        rcsOrderSummaryMapper.updateOrInsertOrOddsValueMax(rcsOrderSummaries, sportId, matchId, playId, marketId);
    }

}
