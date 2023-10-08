package com.panda.sport.rcs.job;

import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.IBastetBallService;
import com.panda.sport.rcs.service.ITagService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.panda.sport.rcs.common.constants.RedisConstants.USER_BOUNDARY_TIME;

/**
 * 专注/部分篮球赌客转ufo-$或好脚
 */
@JobHandler(value = "basketBallCheckTagJob")
@Component
public class BasketBallCheckTagJob extends IJobHandler {


    Logger log = LoggerFactory.getLogger(BasketBallCheckTagJob.class);

    @Autowired
    ITagService tagService;
    @Autowired
    RedisService redisService;
    @Autowired
    IBastetBallService bastetBallService;

    /**
     * 1847 【操盘风控】专注/部分篮球赌客转ufo-$或好脚
     * 需求描述
     * 一、背景：针对1327需求产生的专注篮球赌客、部分投篮球赌客，如果存在UFO-$或者好脚，目前没办法自动变更标签
     * 二、需求：
     * 1、每天5:30am，针对“(当前自然日-注册日期)/28”可以整除的专注篮球赌客、部分投篮球赌客，分别跑UFO-$、好脚的标签规则，如果符合其中某个标签规则，则给用户打上该标签；
     * 2、打上标签后，需要记录日志到用户变更日志表中，从而在用户中心-用户变更日志中、以及用户列表-投注特征标签历史记录中都可以看到。
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        String runkey = RedisConstants.PREFIX + "basketBallCheckTagJob";
        if (!redisService.setIfAbsent(runkey, "1", 3600L)) {
            log.info("BasketBallCheckTagJob 任务执行中...此次不处理");
        }
        try {
            Long time = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis());
            //此参数用于 指定时间 正常情况不使用
            if (StringUtils.isNotEmpty(s) && s.length() == 13) {
                time = Long.valueOf(s);
            }
            time = LocalDateTimeUtil.getDayStartTime(time);
            log.info("篮球赌客转ufo-$或好脚开始" + time);
            bastetBallService.executeBasetToOther(time);
            log.info("篮球赌客转ufo-$或好脚结束");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("篮球赌客转ufo-$或好脚异常:{}:{}", e, e.getMessage());
            return new ReturnT<>(0, e.getMessage());
        } finally {
            redisService.delete(runkey);
        }
        return ReturnT.SUCCESS;
    }

}
