package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.mapper.OrderMapper;
import com.panda.rcs.cleanup.utils.DataUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
@JobHandler(value = "orderExtCleanupDataJob")
public class OrderExtCleanupDataJob extends IJobHandler {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Long starTime = System.currentTimeMillis();
        int rows;
        int totalRwos = 0;

        //上一天按小时清理
        for (int h = 0; h < 24; h ++){
            rows = orderMapper.deleteOrderDetailExt(DataUtils.getTimestampByHour(1, h));
            log.info("::接拒单表数据清理::，本次清理数据->{}", rows);
            totalRwos += rows;
            Thread.sleep(5000);
        }

        Date date = new Date();
        //今天清理执行前的小时数据
        for (int h = 0; h < date.getHours() - 1; h ++){
            rows = orderMapper.deleteOrderDetailExt(DataUtils.getTimestampByHour(0, h));
            log.info("::接拒单表数据清理::，本次清理数据->{}", rows);
            totalRwos += rows;
            Thread.sleep(5000);
        }

        log.info("::接拒单表数据清理::本次一共清理数据->{}条，本次清理耗时->{}毫秒", totalRwos, System.currentTimeMillis() - starTime);
        return ReturnT.SUCCESS;
    }

}
