package com.panda.sport.rcs.job;

import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsByIp;
import com.panda.sport.rcs.db.service.IRiskOrderStatisticsDayByIpService;
import com.panda.sport.rcs.redis.service.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author :  kir
 * @description :  根据IP分组统计每日投注总数
 * @date: 2021-01-30 14:05
 * --------  ---------  --------------------------
 */
@JobHandler(value = "orderStaticsDaysForIpJob")
@Component
public class OrderStaticsDaysForIpJob extends IJobHandler {
    private Logger log = LoggerFactory.getLogger(OrderStaticsDaysForIpJob.class);

    @Autowired
    private IRiskOrderStatisticsDayByIpService service;

    @Autowired
    private RedisService redisService;

    //每次统计的开始时间
    private final static String startTimeKey = RedisConstants.PREFIX + "orderStaticsDaysForIpJob:startTime";

    //通过该key判断上次任务是否执行完成，避免阻塞
    private final static String runkey = RedisConstants.PREFIX + "orderStaticsDaysForIpJob:runKey";

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        log.info("--------------执行'根据IP分组统计每日投注总数'定时任务--------------");
        //设置统计开始时间与结束时间
        Long startTime = System.currentTimeMillis();
        Long endTime = System.currentTimeMillis();

        //此参数用于 指定时间 正常情况不使用
        if (StringUtils.isNotEmpty(s)) {
            String[] dates = s.split(",");
            startTime = Long.valueOf(dates[0]);
            endTime = Long.valueOf(dates[1]);
        } else {
            //如果手动补数据(带参进入)则不进行同步判断，每次自动定时跑任务时进行同步判断避免数据重复叠加
            if (!redisService.setIfAbsent(runkey, "1", 3600L)) {
                log.info("orderStaticsDaysForIpJob 任务执行中...此次不处理");
                log.info("--------------退出'根据IP分组统计每日投注总数'定时任务--------------");
                return ReturnT.FAIL;
            }
            //是否为第一次执行该任务，如果是第一次则从当前天的最开始时间开始统计到当前时间的时分秒；否则取redis中的时间统计到当前时间的时分秒
            if (redisService.get(startTimeKey) == null) {
                //获取今天最开始的时间
                startTime = LocalDateTimeUtil.getDayStartTime(startTime);
            }else{
                startTime = Long.parseLong(redisService.get(startTimeKey).toString());
                //如果开始时间和结束时间大于一天的话，则只统计一天的数据
                if((endTime-startTime)>LocalDateTimeUtil.dayMill){
                    endTime = LocalDateTimeUtil.getDayStartTime(startTime)+LocalDateTimeUtil.dayMill;
                }
            }
        }

        try {
            service.staticsOrderForIp(startTime, endTime, s, 1);
            if(StringUtils.isEmpty(s)){
                redisService.set(startTimeKey, endTime);
            }
            XxlJobLogger.log("已成功执行完。");
        } catch (Exception ex) {
            log.error("-------------- orderStaticsDaysForIpJob任务执行出错--------------");
            log.error(ex.getMessage(), ex);
            XxlJobLogger.log(ex.getMessage(), ex);
        } finally {
            redisService.delete(runkey);
            log.info("--------------'根据IP分组统计每日投注总数'定时任务执行完成--------------");
        }
        return ReturnT.SUCCESS;
    }
}
