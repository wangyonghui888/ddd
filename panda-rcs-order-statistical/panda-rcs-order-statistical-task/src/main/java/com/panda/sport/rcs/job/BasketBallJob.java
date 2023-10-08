package com.panda.sport.rcs.job;

import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.RcsConsumer;
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

@JobHandler(value = "basketBallJob")
@Component
public class BasketBallJob extends IJobHandler {


    Logger log = LoggerFactory.getLogger(BasketBallJob.class);

    @Autowired
    IBastetBallService bastetBallService;
    @Autowired
    RedisService redisService;

    /**
     * 需求描述
     * 1、在标签管理中手动新增2个标签：专注篮球赌客、部分投篮球赌客
     * 2、每天凌晨4点，跑批量脚本，将前一自然日投注篮球的正常用户，以及前一自然日投注篮球的”专注篮球赌客“投注特征标签的用户拉出来，按下面的方式处理：
     * （1）针对注单表中只投注篮球的正常用户，给用户打上”专注篮球赌客“的标签，然后：
     * A、如果用户篮球胜率>50%，则设置用户特殊货量的篮球赛种货量百分比设置为80%；
     * B、如果用户篮球胜率<=50%，则设置用户特殊货量的篮球赛种货量百分比设置为20%；
     * （2）针对注单表中不止投注篮球的正常用户，给用户打上”部分投注篮球赌客“的标签，然后：设置用户特殊货量的篮球赛种货量百分比为50%；
     * （3）针对专注篮球赌客：
     * A、如果用户注单表中有投注其他赛种，则将用户投注特征标签变更为”部分投注篮球赌客“，且设置用户特殊货量的篮球赛种货量百分比设置为50%；
     * B、如果用户注单表中未投注其他赛种：
     * a、如果用户篮球胜率>50%，则设置用户特殊货量的篮球赛种货量百分比设置为80%；
     * b、如果用户篮球胜率<=50%，则设置用户特殊货量的篮球赛种货量百分比设置为20%；
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        String runkey = RedisConstants.PREFIX + "basketBallJob";
        if (!redisService.setIfAbsent(runkey, "1", 24 * 3600L)) {
            log.info("basketBallJob 任务执行中...此次不处理");
        }
        try {
            Long time = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis());
            //此参数用于 指定时间 正常情况不使用
            if (StringUtils.isNotEmpty(s) && s.length() == 13) {
                time = Long.valueOf(s);
            }
            time = LocalDateTimeUtil.getDayStartTime(time);
            log.info("basketBallJob扫描开始" + time);
            bastetBallService.execute(time);
            log.info("basketBallJob扫描结束");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("basketBallJob扫描异常:{}", e);
            return new ReturnT<>(0, e.getMessage());
        } finally {
            redisService.delete(runkey);
        }
        return ReturnT.SUCCESS;
    }
}
