package com.panda.sport.rcs.mgr.service;

import com.panda.merge.dto.Response;
import com.panda.merge.dto.StandardMatchMarketDTO;

/**
 * @Project Name: panda-rcs-trade-group
 * @Package Name: com.panda.sport.rcs.trade.wrapper
 * @Description : 盘口状态服务类
 * @Author : Paca
 * @Date : 2020-07-17 11:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public interface MarketStatusService {

    /**
     * 修改盘口赔率
     *
     * @param standardMatchMarketDTO
     * @return
     * @author Paca
     */
    Response updateMarketOddsNew(StandardMatchMarketDTO standardMatchMarketDTO);

}
