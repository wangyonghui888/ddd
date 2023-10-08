package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.mapper.LoggingMapper;
import com.panda.rcs.cleanup.utils.DataUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobHandler(value = "traderMessageCleanJob")
public class TraderMessageCleanJob extends IJobHandler {

    @Autowired
    private LoggingMapper loggingMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        int rows = loggingMapper.deleteTraderMessageByTime(DataUtils.getCurrTime(60));
        log.info("::rcs_trader_message表数据清理::，本次清理数据->{}", rows);

        rows = loggingMapper.deleteLogRecordByTime(DataUtils.getCurrTime(15));
        log.info("::rcs_log_record表数据清理::，本次清理数据->{}", rows);

        rows = loggingMapper.deleteLogFomatByTime(DataUtils.getCurrTime(15));
        log.info("::rcs_log_fomat表数据清理::，本次清理数据->{}", rows);

//        rows = loggingMapper.deleteOperationLogByTime(DataUtils.getCurrTime(90));
//        log.info("::rcs_operation_log表数据清理::，本次清理数据->{}", rows);

        return ReturnT.SUCCESS;
    }
}
