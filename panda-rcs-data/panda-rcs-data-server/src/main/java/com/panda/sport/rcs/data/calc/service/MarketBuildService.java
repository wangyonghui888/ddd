package com.panda.sport.rcs.data.calc.service;

import com.panda.sport.rcs.pojo.config.BuildMarketPlayConfig;

import java.math.BigDecimal;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : 盘口构建服务
 * @Author : Paca
 * @Date : 2022-05-14 10:44
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface MarketBuildService {

    /**
     * 获取盘口差
     *
     * @param matchId
     * @param playId
     * @return
     */
    BigDecimal getMarketHeadGap(Long matchId, Long playId);

    /**
     * 查询篮球构建盘口配置
     *
     * @param matchId
     * @param playId
     * @return
     */
    BuildMarketPlayConfig queryBasketballBuildMarketConfig(Long matchId, Long playId);
}
