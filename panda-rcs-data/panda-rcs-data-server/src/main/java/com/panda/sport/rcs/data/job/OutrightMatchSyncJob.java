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
 * @Description: 冠军赛事xxl-job定时器
 **/
@JobHandler(value = "outrightMatchSyncJob")
@Slf4j
@Component
public class OutrightMatchSyncJob extends IJobHandler {

    @Autowired
    private INoRealTimeDataSyncService iNoRealTimeDataSyncService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("outrightMatchSyncJob:" + s);
        try {
            iNoRealTimeDataSyncService.syncOutrightMatch(s);
        } catch (Exception e) {
            log.error("冠军赛事异常:", e);
        }
        return ReturnT.SUCCESS;
    }
}
