package com.panda.sport.data.rcs.dto.virtual;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 获取虚拟赛事投注限额 请求VO
 *
 * @description:
 * @author: lithan
 * @date: 2020-12-22 14:41:33
 */
@Data
public class BetAmountLimitReqVo implements java.io.Serializable {

    /**
     * 商户ID 预留扩展使用
     */
    Long tenantId;
    /**
     * 用户ID
     */
    Long userId;

    /**
     * 串关类型(单关传1)
     */
    Integer seriesType;
}