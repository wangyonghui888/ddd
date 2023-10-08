package com.panda.sport.rcs.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 暂停接拒单
 */
@Data
@Accessors(chain = true)
public class PauseOrderVo {
    /**
     * 暂停时间
     */
    private Long pauseTime;
    /**
     * 操盘手id
     */
    private Integer traderId;
    /**
     * 操盘手名称
     */
    private String trader;

    /**
     * 订单编号
     */
    private String orderNo;
}
