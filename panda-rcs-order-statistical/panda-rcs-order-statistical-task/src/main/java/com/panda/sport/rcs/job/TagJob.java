package com.panda.sport.rcs.job;

import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.redis.service.RedisService;
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

@JobHandler(value = "tagJob")
@Component
public class TagJob extends IJobHandler {


    Logger log = LoggerFactory.getLogger(TagJob.class);

    @Autowired
    ITagService tagService;
    @Autowired
    RedisService redisService;
    /**
     * 需求
     * 每天下午2点，针对前一个账务日有投注行为（无论投注是否成功）的每一个用户，
     * 按照已有标签规则逐一计算，访问特征类、财务特征类标签，若计算结果符合标签判断条件，
     * 则自动变更用户的这两类标签，并记录变更历史，字段见第5条说明；投注特征类，
     * 若计算出来的新标签与用户原有投注特征标签不一样，
     * V1.0版本仅需按以下字段记录报警记录：报警日期、用户ID、标签类别、当前标签值、建议变更标签、标签得分
     * （格式为：“规则统计时段-规则名-规则输出结果”，多条得分以“；”分割，例如“近3个月-盈利率-67%；近3个月-篮球打洞笔数-56笔”）；
     * V1.1版本设计报警信息展示及处理功能（详见后续的3.6报警功能），届时，针对投注特征标签，
     * 用户计算出来可能有多个标签都符合，如既是专家又是蛇，报警功能只是忠实地计算结果，实际设置为哪一个投注特征标签，由风控人员自行决定。
     * <p>
     * 每天14点
     * 0 0 14 * * ?
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        String runkey = RedisConstants.PREFIX + "tagJob";
        if (!redisService.setIfAbsent(runkey, "1", 3600L)) {
            log.info("tagJob 任务执行中...此次不处理");
        }
        try {
            Long time = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis());
            //此参数用于 指定时间 正常情况不使用
            if (StringUtils.isNotEmpty(s) && s.length() == 13) {
                time = Long.valueOf(s);
            }
            time = LocalDateTimeUtil.getDayStartTime(time);

            //规则判断  时间分割点 如果超过这个点 从redis去规则结果  否则发送给大数据去计算
            String userBoundaryTime = redisService.getString(USER_BOUNDARY_TIME);
            if (StringUtils.isBlank(userBoundaryTime)) {
                userBoundaryTime = "6";
            }
            String key = String.format(RedisConstants.DAY_TAST_STATUS, LocalDateTimeUtil.now("yyyyMMdd"));
            if (LocalDateTime.now().getHour() >= Long.valueOf(userBoundaryTime)) {
                Object data = redisService.get(key);
                redisService.set(key, "1");
                log.info("标记当天开始计算规则" + LocalDateTime.now().getHour());
            }else {
                redisService.delete(key);
            }

            log.info("标签扫描开始"+time);
            tagService.execute(time, null, null);
            log.info("标签扫描结束");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("标签扫描异常:{}:{}", e, e.getMessage());
            return new ReturnT<>(0, e.getMessage());
        }finally {
            redisService.delete(runkey);
        }
        return ReturnT.SUCCESS;
    }

}
