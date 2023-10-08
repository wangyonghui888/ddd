package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.mapper.OrderMapper;
import com.panda.rcs.cleanup.utils.DataUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobHandler(value = "mtsOrderExtCleanupDataJob")
public class MtsOrderExtCleanupDataJob extends IJobHandler {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Long starTime = System.currentTimeMillis();
        int rows = orderMapper.deleteMtsOrderExt(DataUtils.getCurrTime(60));
        log.info("::rcs_mts_order_ext表数据清理::，本次清理数据->{}", rows);
        log.info("::rcs_mts_order_ext数据清理::本次清理耗时->{}", System.currentTimeMillis() - starTime);
        XxlJobLogger.log("rcs_mts_order_ext数据清理，本次清理->{}::本次清理耗时->{}", rows, System.currentTimeMillis() - starTime);
        return ReturnT.SUCCESS;
    }
}
