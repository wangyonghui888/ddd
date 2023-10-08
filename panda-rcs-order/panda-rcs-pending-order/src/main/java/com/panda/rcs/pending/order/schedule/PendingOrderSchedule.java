package com.panda.rcs.pending.order.schedule;

import com.panda.rcs.pending.order.service.PendingOrderHandlerService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时扫描预约中的预约订单进行处理,默认10秒一次
 */
@Slf4j
@Component
@JobHandler(value = "pendingOrderScheduleJob")
public class PendingOrderSchedule extends IJobHandler {

    @Resource
    private PendingOrderHandlerService pendingOrderHandlerService;
    /**
     * 预约订单定时任务
     */

    @Override
    public ReturnT<String> execute(String s) {
        log.info("预约订单触发撮合定时任务开始执行...");
        pendingOrderHandlerService.handlerPendingOrder();
        return ReturnT.SUCCESS;
    }
}