package com.panda.rcs.pending.order.schedule;

import com.panda.rcs.pending.order.service.IOpenOrderAllPlaysService;
import com.panda.rcs.pending.order.utils.RedisLockUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
@JobHandler(value = "openOrderPlaysAllScheduleJob")
public class OpenOrderPlaysAllSchedule extends IJobHandler {

    @Resource
    private IOpenOrderAllPlaysService IOpenOrderAllPlaysService;

    @Autowired
    private RedisLockUtils redisLockUtils;

    /**
     * 开启预约投注早盘
     */

    @Override
    public ReturnT<String> execute(String s) {
        log.info("开启预约投注早盘所有玩法任务开始执行...");
        IOpenOrderAllPlaysService.openOrderPreAllPlays();
        return ReturnT.SUCCESS;
    }
}