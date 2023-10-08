package com.panda.sport.rcs.data.job;

import com.panda.sport.rcs.data.sync.INoRealTimeDataSyncService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @Description: xxl-job定时器
 **/
@JobHandler(value = "rcsDataInJob")
@Component
public class DataSyncJob extends IJobHandler {

    @Autowired
    INoRealTimeDataSyncService iNoRealTimeDataSyncService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        return null;
    }
}
