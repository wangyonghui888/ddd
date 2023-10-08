package com.panda.sport.rcs.limit.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 商户每日限额预警入参
 * @Author : Paca
 * @Date : 2021-11-27 14:45
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MerchantDailyLimitReqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户ID集合
     */
    private List<String> businessIds;

    /**
     * 已用额度百分比
     */
    private BigDecimal percentageOfUsedQuota;

    public BigDecimal getPercentageOfUsedQuota() {
        if (percentageOfUsedQuota == null || percentageOfUsedQuota.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("0.8");
        }
        return percentageOfUsedQuota;
    }
}
