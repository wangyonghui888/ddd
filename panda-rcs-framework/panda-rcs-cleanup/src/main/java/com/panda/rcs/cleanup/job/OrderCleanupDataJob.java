package com.panda.rcs.cleanup.job;

import com.panda.rcs.cleanup.mapper.OrderMapper;
import com.panda.rcs.cleanup.utils.DataUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@JobHandler(value = "orderCleanupDataJob")
public class OrderCleanupDataJob extends IJobHandler {

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        Long starTime = System.currentTimeMillis();
        int rows = orderMapper.deleteOrder(DataUtils.getTimestamp(7));
        log.info("::订单表数据清理::，本次清理数据->{}", rows);
        rows = orderMapper.deleteOrderDetail(DataUtils.getTimestamp(7));
        log.info("::订单明细表数据清理::，本次清理数据->{}", rows);
        log.info("::订单表数据清理::本次清理耗时->{}", System.currentTimeMillis() - starTime);
        return ReturnT.SUCCESS;
    }

}
