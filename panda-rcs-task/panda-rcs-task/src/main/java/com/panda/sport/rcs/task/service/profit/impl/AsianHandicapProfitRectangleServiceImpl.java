package com.panda.sport.rcs.task.service.profit.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.rcs.core.utils.JsonFormatUtils;

import com.panda.sport.rcs.pojo.statistics.RcsProfitMarket;
import com.panda.sport.rcs.pojo.statistics.RcsProfitRectangle;
import com.panda.sport.rcs.task.job.operation.ProfitRectangleVo;
import com.panda.sport.rcs.task.service.profit.AbstractProfitRectangle;
import com.panda.sport.rcs.task.service.profit.IProfitRectangle;
import com.panda.sport.rcs.utils.MarketValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author :  toney
 * @Project Name :  panda-rcs
 * @Package Name :  com.panda.sport.rcs.mgr.operation.order.impl
 * @Description :  让球盘期望详情处理
 * 让球: 4: 全场让球 19:亚盘让球-上半场
 * @Date: 2019-12-13 11:13
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Component
@Slf4j
public class AsianHandicapProfitRectangleServiceImpl extends AbstractProfitRectangle implements IProfitRectangle {
    /**
     * @return void
     * @Description 处理
     * @Param [orderBean]
     * @Author toney
     * @Date 12:24 2019/12/13
     **/
    @Override
    public void handle(ProfitRectangleVo rectangleVo) {
        handleData(rectangleVo);
    }
    /**
     * @Description   校验规则
     * @Param []
     * @Author  toney
     * @Date  9:49 2019/12/19
     * @return java.lang.Boolean
     **/
    @Override
    public Boolean checkParams(ProfitRectangleVo rectangleVo) {
        if (rectangleVo.getPlayId() == 4 || rectangleVo.getPlayId() == 19) {
            return true;
        }
        return false;
    }
    /**
     * @Description   初始化矩阵起始参数
     * @Param [playId]
     * @Author  toney
     * @Date  9:56 2019/12/19
     * @return void
     **/
    @Override
    public void initRectangleParam(Integer playId) {
        MIN_MATRIX_VALUE = -12.0;
        MAX_MATRIX_VALUE = 12.0;
    }

    /**
    * 亚洲让球盘
    *
    * @param rectangleVo
    */
    @Override
    public void logicHandle(ProfitRectangleVo rectangleVo) {
        QueryWrapper<RcsProfitMarket> queryWrapper = new QueryWrapper();
        queryWrapper.eq("match_id", rectangleVo.getMatchId());
        queryWrapper.eq("play_id", rectangleVo.getPlayId());
        queryWrapper.eq("match_type", rectangleVo.getMatchType());
        List<RcsProfitMarket> rcsProfitMarketList = rcsProfitMarketService.list(queryWrapper);
        String txt = "";
        log.info("期望详情，让球RcsProfitMarket实体类bean{}", JsonFormatUtils.toJson(rcsProfitMarketList));
        for (Double i = MIN_MATRIX_VALUE; i <= MAX_MATRIX_VALUE; i++) {
            RcsProfitRectangle rcsProfitRectangle =  map.get(i);

            for (RcsProfitMarket profitMarket : rcsProfitMarketList) {
                List<Double> marketValueList = MarketValueUtils.splitMarketList(profitMarket.getMarketValue());

                for (Double m : marketValueList) {
                    Integer compareResult = MarketValueUtils.compareHomeMarketValue(m, i);
                    BigDecimal amount = BigDecimal.ZERO;

                    //盘口值和当前矩阵值相等时，走水
                    if (m + rcsProfitRectangle.getScore().doubleValue() == 0) {
                        continue;
                    }
                    //主队让球
                    else if (compareResult == 2) {
                        //判断是否为*.25或者*.75,赢刚赢一半
                        if (marketValueList.size() == 2) {
                            amount = profitMarket.getAddition2().divide(BigDecimal.valueOf(2));
                        } else {
                            amount = profitMarket.getAddition2();
                        }
                    }
                    //客队让球
                    else if (compareResult == 1) {
                        //赢
                        //判断是否为*.25或者*.75,赢刚赢一半
                        if (marketValueList.size() == 2) {
                            amount = profitMarket.getAddition1().divide(BigDecimal.valueOf(2));
                        } else {
                            amount = profitMarket.getAddition1();
                        }
                    }
                    rcsProfitRectangle.setProfitValue(rcsProfitRectangle.getProfitValue().add(amount));
                    rcsProfitRectangle.setUpdateTime(new Date());
                    rcsProfitRectangle.setMatchType(rectangleVo.getMatchType());
                }
            }
        }

        log.info("期望详情，计算过程{}", txt);
        log.info("期望详情{},map实体bean{}",JsonFormatUtils.toJson(map));
    }
}
