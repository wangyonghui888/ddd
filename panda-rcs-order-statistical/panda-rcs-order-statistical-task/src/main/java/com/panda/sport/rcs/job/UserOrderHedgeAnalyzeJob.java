package com.panda.sport.rcs.job;

import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.IUserOrderHedgeAnalyzeService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.job
 * @description :  分析玩家订单是否存在对赌投注. 不管是否存在对赌投注,都将订单是否对赌写在指定订单中
 * @date: 2020-06-28 9:37
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@JobHandler(value = "userOrderHedgeAnalyzeJob")
@Component
public class UserOrderHedgeAnalyzeJob extends IJobHandler {

    @Autowired
    IUserOrderHedgeAnalyzeService userOrderHedgeAnalyzeService;


    Logger log = LoggerFactory.getLogger(UserOrderHedgeAnalyzeJob.class);

    @Autowired
    RedisService redisService;
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        String runkey = RedisConstants.PREFIX + "userOrderHedgeAnalyzeJob";
        if (!redisService.setIfAbsent(runkey, "1", 3600L)) {
            log.info("userOrderHedgeAnalyzeJob 任务执行中...此次不处理");
        }

        Long beginTime = System.currentTimeMillis();
        if (StringUtils.isNotEmpty(s) && s.length() == 13) {
            beginTime = Long.valueOf(s);
        }
        beginTime = beginTime - 30 * 60 * 1000L;

        try {
            userOrderHedgeAnalyzeService.analyzeUserOrderHedge(beginTime);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            redisService.delete(runkey);
        }


        return ReturnT.SUCCESS;
    }
}
