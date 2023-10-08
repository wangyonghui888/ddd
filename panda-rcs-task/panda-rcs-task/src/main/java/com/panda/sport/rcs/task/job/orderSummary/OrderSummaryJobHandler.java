package com.panda.sport.rcs.task.job.orderSummary;

import com.panda.sport.rcs.pojo.RcsCode;
import com.panda.sport.rcs.task.wrapper.RcsCodeService;
import com.panda.sport.rcs.task.wrapper.RcsOrderSummaryService;
import com.panda.sport.rcs.task.wrapper.order.ITOrderDetailService;
import com.panda.sport.rcs.vo.OrderSummaryVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * @Description 定时处理订单投注项金额最大的赔率
 * @Param
 * @Author kimi
 * @Date 2020/7/8
 * @return
 **/
@JobHandler(value = "orderSummaryJobHandler")
@Component
@Slf4j
public class OrderSummaryJobHandler extends IJobHandler {
    @Autowired
    private ITOrderDetailService itOrderDetailService;
    @Autowired
    private RcsOrderSummaryService rcsOrderSummaryService;
    @Autowired
    private RcsCodeService rcsCodeService;
    /**
     * 是代表早盘 目前是只计算早盘的
     */
    private static final Integer MATCH_TYPE = 1;
    /**
     * 定时任务的执行时间  上一次
     */
    private static final String SNAP_SHOT_PROCESSING_TIME = "snap_shot_processing_time";

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        // 暂时只做体育
        Long sportId = 1L;
        // 1:扫描订单数据库
        try {
            List<RcsCode> rcsCodeList = rcsCodeService.selectRcsCods(SNAP_SHOT_PROCESSING_TIME);
            if (CollectionUtils.isEmpty(rcsCodeList)) {
                log.error("赛前快照未配置数据库");
                return FAIL;
            }
            long beginTime = Long.parseLong(rcsCodeList.get(0).getValue());
            long endTime = System.currentTimeMillis();
            List<Long> oddsId = itOrderDetailService.getOddsId(sportId, beginTime, endTime);
            List<OrderSummaryVo> tOrderDetailUpBetTime = new ArrayList<>();
            if (!CollectionUtils.isEmpty(oddsId)) {
                tOrderDetailUpBetTime = itOrderDetailService.getTOrderDetailUpBetTime(sportId, MATCH_TYPE, oddsId);
            }
            HashMap<Long, OrderSummaryVo> orderSummaryVoHashMap = new HashMap<>();
            // 2:有订单的投注项需要重新进行计算
            if (CollectionUtils.isEmpty(tOrderDetailUpBetTime)) {
                log.info("没有新的注单");
                return SUCCESS;
            }
            for (OrderSummaryVo orderSummaryVo : tOrderDetailUpBetTime) {
                if (orderSummaryVoHashMap.containsKey(orderSummaryVo.getPlayOptionsId())) {
                    OrderSummaryVo orderSummaryVo1 = orderSummaryVoHashMap.get(orderSummaryVo.getPlayOptionsId());
                    if (orderSummaryVo.getBetAmount() > orderSummaryVo1.getBetAmount()) {
                        orderSummaryVoHashMap.put(orderSummaryVo.getPlayOptionsId(), orderSummaryVo);
                    }
                } else {
                    orderSummaryVoHashMap.put(orderSummaryVo.getPlayOptionsId(), orderSummaryVo);
                }
            }
            Collection<OrderSummaryVo> values = orderSummaryVoHashMap.values();
            rcsOrderSummaryService.insertOrUpdateOddsValueMax(values);
            rcsCodeList.get(0).setValue(String.valueOf(endTime));
            rcsCodeService.updateById(rcsCodeList.get(0));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return SUCCESS;
    }
}
