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
 * @Description: 赛种同步xxl-job定时器
 **/
@JobHandler(value = "sportTypeSyncJob")
@Slf4j
@Component
public class SportTypeSyncJob extends IJobHandler {

    @Autowired
    private INoRealTimeDataSyncService iNoRealTimeDataSyncService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("sportTypeSyncJob:" + s);
        try {
            iNoRealTimeDataSyncService.syncSportTypeData(s);
        } catch (Exception e) {
            log.error("体育种类同步异常:", e);
        }
        return ReturnT.SUCCESS;
    }
}
