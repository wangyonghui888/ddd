package com.panda.sport.rcs.task.job.operation;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panda.sport.rcs.constants.RedisKeys;
import com.panda.sport.rcs.core.cache.client.RedisClient;
import com.panda.sport.rcs.pojo.TOrderDetail;
import com.panda.sport.rcs.pojo.statistics.RcsMatchDimensionStatistics;
import com.panda.sport.rcs.task.wrapper.order.ITOrderDetailService;
import com.panda.sport.rcs.task.wrapper.statistics.RcsMatchDimensionStatisticsService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;


/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.task.job.operation
 * @Description :  数据落库
 * @Date: 2020-01-06 13:30
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@JobHandler(value="matchRealtimeValueFixDataJobHandler")
@Component
public class MatchRealtimeValueFixDataJobHandler extends IJobHandler {
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private ITOrderDetailService orderDetailService;

    @Autowired
    private RcsMatchDimensionStatisticsService rcsMatchDimensionStatisticsService;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        XxlJobLogger.log("matchProfitFixDataJobHandler，开始修复数据赛事id:." + param);


        QueryWrapper<TOrderDetail> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("match_id", param);

        List<TOrderDetail> orderDetailList = orderDetailService.list(queryWrapper);


        RcsMatchDimensionStatistics bean = new RcsMatchDimensionStatistics();
        for (TOrderDetail orderDetail : orderDetailList) {
            bean.setTotalValue(bean.getTotalValue().add(BigDecimal.valueOf(orderDetail.getBetAmount())));
            bean.setTotalOrderNums(bean.getTotalOrderNums() + 1);
        }

        QueryWrapper<RcsMatchDimensionStatistics> statisticsQueryWrapper = new QueryWrapper<>();
        statisticsQueryWrapper.eq("match_id", param);
        RcsMatchDimensionStatistics statistics = rcsMatchDimensionStatisticsService.getOne(statisticsQueryWrapper);
        if (statistics == null) {
            rcsMatchDimensionStatisticsService.save(bean);
        } else {
            rcsMatchDimensionStatisticsService.updateById(bean);
        }



        redisClient.hSet(RedisKeys.REAL_TIME_VOLUME_BY_MATCH_DIMENSION_REDIS_CACHE, bean.getMatchId().toString(),
                bean.getTotalValue().toString());

        redisClient.hSet(RedisKeys.SUM_MATCH_ORDER_NUMS_REDIS_CACHE, bean.getMatchId().toString(),
                bean.getTotalOrderNums().toString());
        ;

        //向ws发送消息
        rcsMatchDimensionStatisticsService.sendSysnData(bean.getMatchId());

        XxlJobLogger.log("matchProfitFixDataJobHandler处理完毕!!!matchdId:" + param);
        return SUCCESS;
    }
}
