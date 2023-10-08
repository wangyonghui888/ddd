package com.panda.sport.rcs.job;

import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.IDangerousOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 危险投注扫描-蛇单
 */
@JobHandler(value = "dangerousSnakeOrderJob")
@Component
public class DangerousSnakeOrderJob extends IJobHandler {


    Logger log = LoggerFactory.getLogger(DangerousSnakeOrderJob.class);

    @Autowired
    IDangerousOrderService dangerousOrderService;
    @Autowired
    RedisService redisService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        try {
            //log.info("危险投注-蛇单订单开始");
            dangerousOrderService.executeSnake();
            //log.info("危险投注-蛇单订单结束");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("危险投注-蛇单订单扫描异常:{}", e);
            return new ReturnT<>(0,e.getMessage());
        }
        return ReturnT.SUCCESS;
    }
}
