package com.panda.sport.rcs.job;

import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.IDangerousOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 危险投注扫描
 */
@JobHandler(value = "dangerousOrderJob")
@Component
public class DangerousOrderJob extends IJobHandler {


    Logger log = LoggerFactory.getLogger(DangerousOrderJob.class);

    @Autowired
    IDangerousOrderService dangerousOrderService;
    @Autowired
    RedisService redisService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        String runkey = RedisConstants.PREFIX + "dangerousOrderJob";
        if (!redisService.setIfAbsent(runkey, "1", 3600L)) {
            log.info("dangerousOrderJob 任务执行中...此次不处理");
        }
        //此参数用于重置 最后统计时间 正常情况不使用
        if (StringUtils.isNotEmpty(s) && s.length() == 13) {
            String key = String.format("%s.%s", RedisConstants.PREFIX, "dangerous.order.lasttime");
            redisService.set(key, Long.valueOf(s));
        }

        try {
            log.info("危险投注订单开始");
            dangerousOrderService.execute(LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis()));
            log.info("危险投注订单结束");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("危险投注订单扫描异常:{}", e);
            return new ReturnT<>(0,e.getMessage());
        }finally {
            redisService.delete(runkey);
        }
        return ReturnT.SUCCESS;
    }
}
