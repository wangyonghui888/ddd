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
@JobHandler(value = "orderVolumeCleanupJob")
public class OrderVolumeCleanupJob extends IJobHandler {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        int days = null != s && !"".equals(s) ? Integer.parseInt(s) : 3;
        Long startTime = System.currentTimeMillis();
        int rows;
        int totalRows = 0;
        //上一天按小时清理
        for (int h = 0; h < 24; h ++){
            rows = orderMapper.deleteOrderVolumeByTime(DataUtils.getTimestampByHour(days, h));
            log.info("::rcs_order_hide表数据清理::，本次清理数据->{}", rows);
            totalRows += rows;
            Thread.sleep(5000);
        }

        XxlJobLogger.log("rcs_order_hide表数据清理,本次清理数据->{}, 耗时:{}", totalRows, System.currentTimeMillis() - startTime);
        return ReturnT.SUCCESS;
    }


}
