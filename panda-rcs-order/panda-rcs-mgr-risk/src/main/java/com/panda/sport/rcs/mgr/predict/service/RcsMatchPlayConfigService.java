package com.panda.sport.rcs.mgr.predict.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.panda.sport.rcs.pojo.RcsMatchMarketConfig;
import com.panda.sport.rcs.pojo.RcsMatchPlayConfig;

/**
 * @author :  Administrator
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.trade.wrapper.impl
 * @Description :  TODO
 * @Date: 2019-11-08 20:18
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

public interface RcsMatchPlayConfigService extends IService<RcsMatchPlayConfig> {

    RcsMatchPlayConfig selectRcsMatchPlayConfig(RcsMatchMarketConfig config);

    void insertOrUpdateMarketHeadGap(RcsMatchMarketConfig config);
//
//    void inserOrUpdateList(Long matchId, List<Integer> playId, Integer status, Integer dataSource);

}
