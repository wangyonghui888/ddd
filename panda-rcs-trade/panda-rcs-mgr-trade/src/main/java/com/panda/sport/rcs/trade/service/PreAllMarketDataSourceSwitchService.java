package com.panda.sport.rcs.trade.service;

import com.panda.sport.rcs.vo.HttpResponse;
import com.panda.sport.rcs.vo.PreAllMarketDataSourceVo;

public interface PreAllMarketDataSourceSwitchService {

    void dataSourceSwitch(String before, String after, Integer userId);

    HttpResponse batchRestoreDataSource(int maxDay, PreAllMarketDataSourceVo preAllMarketDataSourceVo, Integer userId);
}
