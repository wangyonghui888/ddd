package com.panda.sport.rcs.job;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsByIp;
import com.panda.sport.rcs.db.service.IOrderStaticsForIpService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author :  kir
 * @description :  每小时扫描总表8天内所有投注过得IP将7天内每日数据表存在的数据总金额累加到总表
 * @date: 2021-01-30 14:05
 * -------------------------------------------
 */
@JobHandler(value = "orderStaticsForDayStartDataJob")
@Component
public class OrderStaticsForDayStartDataJob extends IJobHandler {
    private Logger log = LoggerFactory.getLogger(OrderStaticsForDayStartDataJob.class);

    @Autowired
    private IOrderStaticsForIpService service;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        log.info("--------------执行'risk_order_statistics_by_ip表7天数据统计初始化'定时任务--------------");
        try {
            //最后投注时间 统计开始时间
            Long finalBeginTime = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis()) - LocalDateTimeUtil.dayMill*7;
            //最后投注时间 统计结束时间
            Long finalEndTime = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis()) + LocalDateTimeUtil.dayMill;
            //每日统计时间 开始时间
            Long staticBeginTime = LocalDateTimeUtil.milliToDate(LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis()) - LocalDateTimeUtil.dayMill*6);
            //每日统计时间 结束时间
            Long staticEndTime = LocalDateTimeUtil.milliToDate(LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis()) + LocalDateTimeUtil.dayMill);
            //总条数
            Integer count = service.queryAmountByFinalBetTimeCount(staticBeginTime, staticEndTime, finalBeginTime, finalEndTime);
            log.info("--------------7日统计数据共查处 "+count+" 条--------------");
            //分批处理
            service.queryAmountByFinalBetTime(staticBeginTime, staticEndTime, finalBeginTime, finalEndTime, count);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            XxlJobLogger.log(ex.getMessage(), ex);
        }
        log.info("--------------'risk_order_statistics_by_ip表7天数据统计初始化'任务执行完成--------------");
        return ReturnT.SUCCESS;
    }
}
