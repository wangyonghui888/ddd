package com.panda.sport.rcs.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  TODO
 * @Date: 2019-12-12 10:32
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class OrderDetailVo {
    /**
     * 用户id
     **/
    private String uid;
    /**
     * 注单赔率
     **/
    private BigDecimal oddsValue;
    /**
     * 注单金额
     **/
    private BigDecimal betAmount;
    /**
     * 注单金额币种
     **/
    private String currencyCode;
    /**
     * 下注时间
     **/
    private String betTime;
    /**
     * 盘口值
     **/
    private String marketValue;
    /**
     * 投注项名称(新加)
     */
    private String playOptionsName;

}
