package com.panda.sport.data.rcs.dto.limit;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  lithan
 * 商户单场限额
 */
@Data
public class RcsQuotaMerchantSingleFieldLimitVo implements Serializable {

    private static final long serialVersionUID = -6921937097000016306L;

    /**
     * 赛事id
     */
    private Long matchId;
    /**
     * 主键
     */
    //private Long id;
    /**
     * 体育种类
     */
//    private Integer sportId;
    /**
     * 联赛等级
     */
    //private Integer templateLevel;
    /**
     * 赔付限额基础值
     */
    //private Long compensationLimitBase;
    /**
     * 早盘赔付限额比例  0.0001-10
     */
    //private BigDecimal earlyMorningPaymentLimitRatio;
    /**
     * 早盘赔付限额
     */
    private Long earlyMorningPaymentLimit;
    /**
     * 滚球赔付限额比例  0.0001-10
     */
    //private BigDecimal liveBallPayoutLimitRatio;
    /**
     * 滚球赔付限额
     */
    private Long liveBallPayoutLimit;
    /**
     * 0 未生效  1生效
     */
    // private Integer status;
}
