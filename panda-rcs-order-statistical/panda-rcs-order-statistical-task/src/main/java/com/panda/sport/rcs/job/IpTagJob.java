package com.panda.sport.rcs.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.panda.sport.rcs.CommonUtils;
import com.panda.sport.rcs.common.utils.CopyUtils;
import com.panda.sport.rcs.common.utils.LocalDateTimeUtil;
import com.panda.sport.rcs.db.entity.RiskOrderStatisticsByIp;
import com.panda.sport.rcs.db.entity.RiskOrderTagIp;
import com.panda.sport.rcs.db.entity.RiskUserVisitIp;
import com.panda.sport.rcs.db.entity.RiskUserVisitIpTag;
import com.panda.sport.rcs.db.service.IOrderStaticsForIpService;
import com.panda.sport.rcs.db.service.IRiskOrderTagIpService;
import com.panda.sport.rcs.db.service.IRiskUserVisitIpService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author :  kir
 * @description :  用户IP及标签统计表 定时任务
 * @date: 2021-02-02 14:05
 * --------  ---------  --------------------------
 */
@JobHandler(value = "ipTagJob")
@Component
public class IpTagJob extends IJobHandler {
    private Logger log = LoggerFactory.getLogger(IpTagJob.class);

    @Autowired
    private IRiskUserVisitIpService ipService;

    @Autowired
    private IRiskOrderTagIpService orderTagIpService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        CommonUtils.mdcPut();
        log.info("--------------执行'用户IP及标签统计表'定时任务--------------");
        try {
            //获取当前的开始时间和结束时间
            Long startTime = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis());
            Long endTime = LocalDateTimeUtil.getDayStartTime(System.currentTimeMillis())+LocalDateTimeUtil.dayMill;

            //如果有带参数，则根据参数的开始时间与结束时间进行统计
            if(StringUtils.isNotEmpty(s)){
                String[] dates = s.split(",");
                startTime = Long.valueOf(dates[0]);
                endTime = Long.valueOf(dates[1]);
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
            log.info("--------------统计结束--------------");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            XxlJobLogger.log(ex.getMessage(), ex);
        }
        log.info("--------------'用户IP及标签统计表'任务执行完成--------------");
        return ReturnT.SUCCESS;
    }
}
