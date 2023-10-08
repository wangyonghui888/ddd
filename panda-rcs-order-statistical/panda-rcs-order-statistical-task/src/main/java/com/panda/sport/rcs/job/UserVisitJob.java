package com.panda.sport.rcs.job;

import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.common.utils.ThreadUtil;
import com.panda.sport.rcs.redis.service.RedisService;
import com.panda.sport.rcs.service.IUserVisitService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author :  lithan
 * 访问特征-用户登录ip记录 任务
 * @date: 2020-06-27 10:14:48
 */
@JobHandler(value = "userVisitJob")
@Component
public class UserVisitJob extends IJobHandler {

    Logger log = LoggerFactory.getLogger(UserVisitJob.class);
    @Autowired
    IUserVisitService userVisitService;
    @Autowired
    RedisService redisService;

    /**
     * 每10分钟触发一次
     * 0 0/10 * * * ?
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        String runkey = RedisConstants.PREFIX + "userVisitJob";
        if (!redisService.setIfAbsent(runkey, "1", 3600L)) {
            log.info("userVisitJob 任务执行中...此次不处理");
        }

        //此参数用于重置 最后统计时间 正常情况不使用
        if (StringUtils.isNotEmpty(s) && s.length() == 13) {
            String key = String.format("%s.%s", RedisConstants.PREFIX, "statics.ip.lasttime");
            redisService.set(key, Long.valueOf(s));
        }

        try {
            log.info("用户IP数据统计开始" + s);
            userVisitService.userIpStatics(LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis()));
            log.info("用户IP数据统计结束");
        } catch (Exception e) {
            log.info("用户IP数据统计开始异常:{}", e);
            return new ReturnT<>(0, e.getMessage());
        }finally {
            redisService.delete(runkey);
        }
        return ReturnT.SUCCESS;
    }
}
