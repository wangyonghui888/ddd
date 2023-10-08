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
 * @Description: 运动员同步xxl-job定时器
 **/
@JobHandler(value = "standardSportPlayerSyncJob")
@Slf4j
@Component
public class StandardSportPlayerSyncJob extends IJobHandler {

    @Autowired
    private INoRealTimeDataSyncService iNoRealTimeDataSyncService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("standardSportPlayerSyncJob:" + s);
        try {
            iNoRealTimeDataSyncService.queryStandardSportPlayer(s);
        } catch (Exception e) {
            log.error("球员信息同步异常:", e);
        }
        return ReturnT.SUCCESS;
    }
}
