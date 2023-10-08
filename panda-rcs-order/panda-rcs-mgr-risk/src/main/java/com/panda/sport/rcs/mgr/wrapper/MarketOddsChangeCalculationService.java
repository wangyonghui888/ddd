package com.panda.sport.rcs.mgr.wrapper;

import com.panda.sport.data.rcs.dto.ThreewayOverLoadTriggerItem;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  根据限额等级重新计算盘口赔率
 * @Date: 2019-10-28 15:24
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface MarketOddsChangeCalculationService {
    /**
     * @Description   根据限额级别重新计算赔率并推送MQ消息到融合
     * @Param [overLoadTriggerItem]
     * @Author  Sean
     * @Date  17:18 2019/10/25
     * @return java.lang.Boolean
     **/
    Boolean calculationOddsByOverLoadTrigger(RcsMatchMarketConfig config,ThreewayOverLoadTriggerItem overLoadTriggerItem);
}
