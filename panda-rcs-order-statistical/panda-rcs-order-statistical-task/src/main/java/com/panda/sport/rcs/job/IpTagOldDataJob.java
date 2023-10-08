package com.panda.sport.rcs.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.common.constants.RedisConstants;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.db.entity.RiskOrderTagIp;
import com.panda.sport.rcs.db.entity.RiskUserVisitIp;
import com.panda.sport.rcs.db.service.IRiskOrderTagIpService;
import com.panda.sport.rcs.db.service.IRiskUserVisitIpService;
import com.panda.sport.rcs.redis.service.RedisService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author :  kir
 * @description :  用户IP及标签统计表（历史数据） 定时任务（从后往前跑）
 * @date: 2021-02-02 14:05
 * --------  ---------  --------------------------
 */
@JobHandler(value = "ipTagOldDataJob")
@Slf4j
@Component
public class IpTagOldDataJob extends IJobHandler {
    private Logger log = LoggerFactory.getLogger(IpTagOldDataJob.class);

    @Autowired
    private IRiskUserVisitIpService ipService;

    @Autowired
    private IRiskOrderTagIpService orderTagIpService;

    @Autowired
    private RedisService redisService;

    //每次统计的开始时间
    private final static String startTimeKey = RedisConstants.PREFIX + "ipTagOldDataJob:startTime";

    //通过该key判断上次任务是否执行完成，避免阻塞
    private final static String runkey = RedisConstants.PREFIX + "ipTagOldDataJob:runKey";

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        log.info("--------------执行'用户IP及标签统计表（历史数据） 定时任务（从后往前跑）'定时任务--------------");
        try {
            //获取当前的开始时间和结束时间
            Long startTime = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis())-LocalDateTimeUtil.dayMill;
            Long endTime = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis());

            //如果有带参数，则根据参数的开始时间与结束时间进行统计
            if(StringUtils.isNotEmpty(s)){
                String[] dates = s.split(",");
                startTime = Long.valueOf(dates[0]);
                endTime = Long.valueOf(dates[1]);
            }else{
                if (redisService.get(startTimeKey) == null) {
                    //第一次进来的时候统计的开始时间和结束时间取上面设定的默认值
                }else{
                    //如果手动补数据(带参进入)则不进行同步判断，每次自动定时跑任务时进行同步判断避免数任务阻塞
                    if (!redisService.setIfAbsent(runkey, "1", 3600L)) {
                        log.info("ipTagOldDataJob 任务执行中...此次不处理");
                        log.info("--------------退出'用户IP及标签统计表（历史数据） 定时任务（从后往前跑）'定时任务--------------");
                        return ReturnT.FAIL;
                    }
                    //如果redis里面有存值，则取redis里面的值作为统计结束时间，往前推一天设定为统计开始时间
                    endTime = Long.parseLong(redisService.get(startTimeKey).toString());
                    startTime = Long.parseLong(redisService.get(startTimeKey).toString())-LocalDateTimeUtil.dayMill;
                }
            }

            //查询时间段内的数据
            List<RiskUserVisitIp> userVisitIpList = ipService.queryListByLoginTime(startTime, endTime);
            List<RiskOrderTagIp> tagIpList = CopyUtils.clone(userVisitIpList, RiskOrderTagIp.class);

            log.info("--------------开始统计--------------");
            for (RiskOrderTagIp riskOrderTagIp : tagIpList) {
                LambdaQueryWrapper<RiskOrderTagIp> warpper = new LambdaQueryWrapper<>();
                warpper.eq(RiskOrderTagIp::getUserId, riskOrderTagIp.getUserId());
                warpper.eq(RiskOrderTagIp::getIp, riskOrderTagIp.getIp());
                RiskOrderTagIp orderTagIp = orderTagIpService.getOne(warpper);
                if(ObjectUtils.isEmpty(orderTagIp)){
                    orderTagIpService.save(riskOrderTagIp);
                }else{
                    riskOrderTagIp.setId(orderTagIp.getId());
                    orderTagIpService.updateById(riskOrderTagIp);
                }
            }
            log.info("共更新{}条数据",tagIpList.size());
            log.info("--------------统计结束--------------");

            redisService.set(startTimeKey, startTime);

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            XxlJobLogger.log(ex.getMessage(), ex);
        } finally {
            redisService.delete(runkey);
            log.info("--------------'用户IP及标签统计表（历史数据） 定时任务（从后往前跑）'定时任务执行完成--------------");
        }
        return ReturnT.SUCCESS;
    }
}
