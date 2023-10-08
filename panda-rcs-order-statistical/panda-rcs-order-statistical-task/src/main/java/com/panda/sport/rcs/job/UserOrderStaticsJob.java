package com.panda.sport.rcs.job;

import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.impl.OrderStaticsServiceImpl;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author :  dorich
 * @description :  统计用户订单的定时任务
 * @date: 2020-06-24 10:05
 * --------  ---------  --------------------------
 */
@JobHandler(value = "userOrderStaticsJob")
@Component
public class UserOrderStaticsJob extends IJobHandler {
    private Logger log = LoggerFactory.getLogger(UserOrderStaticsJob.class);
    @Autowired
    private OrderStaticsServiceImpl orderStaticsService;
    @Autowired
    private RedisService redisService;

    private final static String runkey = RedisConstants.PREFIX + "userOrderStaticsJob:runKey";
    private final static String runTimeKey = RedisConstants.PREFIX + "userOrderStaticsJob:runTime";

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        if (!redisService.setIfAbsent(runkey, "1", 3600L)) {
            log.info("userOrderStaticsJob 任务执行中...此次不处理" + s);
            return ReturnT.SUCCESS;
        }


        /*** 直接使用订单统计服务的接口 ***/
        Long startTime = System.currentTimeMillis();
        Long endTime = System.currentTimeMillis();


        //此参数用于 指定时间 正常情况不使用
        if (StringUtils.isNotEmpty(s)) {
            String[] dates = s.split(",");

            startTime = Long.valueOf(dates[0]);
            endTime = Long.valueOf(dates[1]);
        } else {
            if (redisService.get(runTimeKey) == null) {
                /***  当前时间为0点-1点之前跑得是昨天数据，当前日期减1天 ,其它时间点为当前时间   ***/
                if (LocalDateTime.now().getHour() == 0) {
                    startTime = LocalDateTimeUtil.getDayStartTime(startTime) - LocalDateTimeUtil.dayMill;
                } else {
                    startTime = LocalDateTimeUtil.getDayStartTime(startTime);
                }

            } else {
                startTime = Long.parseLong(redisService.get(runTimeKey).toString());
                //加个优化, 每天3点 和15点   自动重跑近4天的数据  修复某些因为未结算导致不一致的数据
                if (LocalDateTime.now().getDayOfMonth() % 3 == 0 && LocalDateTime.now().getHour() == 3) {
                    log.info("每三天刷一次数据");
                    startTime = endTime - (15 * 24 * 60 * 60 * 1000);
                }
            }
        }

        try {
            orderStaticsService.staticsOrderForUsers(startTime, endTime);
            redisService.set(runTimeKey, endTime);
            XxlJobLogger.log("已成功执行完。");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            XxlJobLogger.log(ex.getMessage(), ex);
        } finally {
            redisService.delete(runkey);
        }
        return ReturnT.SUCCESS;
    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now().getDayOfMonth()%5==0);
    }
}
