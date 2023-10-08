package com.panda.sport.rcs.data.job;

import com.panda.sport.rcs.data.sync.INoRealTimeDataSyncService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @Description: 普通赛事玩法同步xxl-job定时器
 **/
@JobHandler(value = "marketCategorySyncJob")
@Slf4j
@Component
public class MarketCategorySyncJob extends IJobHandler {

    @Autowired
    private INoRealTimeDataSyncService iNoRealTimeDataSyncService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("marketCategorySyncJob:" + s);
        try {
            iNoRealTimeDataSyncService.syncSportMarketCategoryData(s);
        } catch (Exception e) {
            log.error("玩法表/标准玩法投注项表同步错误异常:", e);
        }
        return ReturnT.SUCCESS;
    }
}
