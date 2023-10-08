package com.panda.sport.rcs.task.job.operation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.pojo.TSettle;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.task.wrapper.order.ITOrderDetailService;
import com.panda.sport.rcs.task.wrapper.order.ITSettleService;
import com.panda.sport.rcs.task.wrapper.statistics.RcsMatchDimensionStatisticsService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.job.operation
 * @Description :  已结算修复数据
 * @Date: 2020-01-06 21:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@JobHandler(value="matchSettledFixDataJobHandler")
@Component
public class MatchSettledFixDataJobHandler extends IJobHandler {
    @Autowired
    private RedisClient redisClient;


    @Autowired
    private ITSettleService settleService;

    @Autowired
    private RcsMatchDimensionStatisticsService rcsMatchDimensionStatisticsService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("matchProfitFixDataJobHandler，开始修复数据赛事id:." + param);


        QueryWrapper<TSettle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("match_id",param);
        List<TSettle> settleList = settleService.list(queryWrapper);


        RcsMatchDimensionStatistics bean = new RcsMatchDimensionStatistics();
        for(TSettle settle: settleList){
            bean.setSettledRealTimeValue(bean.getSettledRealTimeValue().add(BigDecimal.valueOf(settle.getSettleAmount())));
            bean.setSettledProfitValue(BigDecimal.valueOf(settle.getBetAmount() - settle.getSettleAmount()));
        }

        QueryWrapper<RcsMatchDimensionStatistics> statisticsQueryWrapper = new QueryWrapper<>();
        statisticsQueryWrapper.eq("match_id",param);
        RcsMatchDimensionStatistics statistics = rcsMatchDimensionStatisticsService.getOne(statisticsQueryWrapper);
        if(statistics == null){
            rcsMatchDimensionStatisticsService.save(bean);
        }else {
            rcsMatchDimensionStatisticsService.updateById(bean);
        }

        //已结算货量
        redisClient.hSet(RedisKeys.SETTLE_REAL_VOLUME_REDIS_CACHE,param,
                bean.getTotalValue().toString());
        //已结算盈亏
        redisClient.hSet(RedisKeys.SETTLE_PROFIT_REDIS_CACHE, param,
                bean.getSettledProfitValue().toString());

        //向ws发送消息
        rcsMatchDimensionStatisticsService.sendSysnData(bean.getMatchId());

        XxlJobLogger.log("matchSettledFixDataJobHandler处理完毕!!!matchdId:"+param);
        return SUCCESS;
    }
}