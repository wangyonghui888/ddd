package com.panda.sport.data.rcs.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderBeforeHandItemVo implements Serializable {
    /**
     * 注单号号
     */
    String betNo;

    /***
     * 盘口ID
     */
    Long marketId;
    /**
     * 投注项id
     */
    Long playOptionsId;

    /**
     * 赔率
     */
    BigDecimal odds;

    /**
     * 盘口状态
     */
    Integer marketStatus;

}
