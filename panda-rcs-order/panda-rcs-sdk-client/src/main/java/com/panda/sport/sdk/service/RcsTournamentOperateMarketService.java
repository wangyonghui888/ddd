package com.panda.sport.sdk.service;

import com.panda.sport.data.rcs.dto.ExtendBean;
import com.panda.sport.rcs.pojo.RcsTournamentMarketConfig;

import java.util.List;
import java.util.Map;

/**
 * @author :  Sean
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.mgr.wrapper
 * @Description :  联赛操盘服务类
 * @Date: 2019-10-23 16:28
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface RcsTournamentOperateMarketService {

    /**
     * @return com.panda.sport.rcs.pojo.RcsTournamentMarketConfig
     * @Description //查找数据
     * @Param [columnMap]
     * @Author kimi
     * @Date 2019/11/13
     **/
    List<RcsTournamentMarketConfig> getRcsTournamentMarketConfig(Map<String, Object> columnMap);

    /**
     * @Description   查询赛事和联赛最大投注额
     * @Param [order]
     * @Author  Sean
     * @Date  16:47 2019/12/6
     * @return com.panda.sport.rcs.pojo.RcsTournamentMarketConfig
     **/
    Long queryMatchAndTournamentMaxBetAmount(ExtendBean order);

}
