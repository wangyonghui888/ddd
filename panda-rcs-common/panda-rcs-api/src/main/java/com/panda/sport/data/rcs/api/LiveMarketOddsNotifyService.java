package com.panda.sport.data.rcs.api;

import com.panda.sport.data.rcs.dto.MatchMarketCategoryDTO;

import java.util.List;

/**
 * 实时盘口赔率通知接口服务类
 */
public interface LiveMarketOddsNotifyService {
    /**
     * 实时盘口赔率变化通知
     *
     * @param marketCategoryDTOs
     * @return
     */
    Response marketOddsChanged(List<MatchMarketCategoryDTO> marketCategoryDTOs);
}
