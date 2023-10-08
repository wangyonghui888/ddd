package com.panda.rcs.pending.order.pojo;

import com.panda.rcs.pending.order.constants.NumberConstant;
import lombok.Data;

@Data
public class LimitRateDto {
    /**
     * 滚球的订单预约速率,赋值默认值
     */
    private Integer liveLimitRate = 100;
    /**
     * 早盘的订单预约速率,赋值默认值
     */
    private Integer earlyLimitRate = 100;

    //统计给业务发送了多少单
    private Integer countNum= NumberConstant.NUM_ZERO;
}
