package com.panda.sport.rcs.pojo.vo.api.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsChampionOddsFieldsResVo {
    /**
     * 盘口id
     */
    private Long marketId;
    /**
     * 投注项ID
     */
    private String oddsType;
    /**
     * 投注项ID
     */
    private String oddsFiedsId;
    /**
     * 投注项名称
     */
    private String oddsFiedsName;
    /**
     * panda赔率
     */
    private BigDecimal oddsValue;
    /**
     * 投注项状态
     */
    private Integer oddsFiedsStatus;

    /**
     * 三方数据投注项状态
     */
    private Integer thirdSourceActive;

    /**
     * 用户单项赔付限额
     */
    private BigDecimal amount;
    /**
     * SR赔率
     */
    private BigDecimal originalOddsValue;

    /**
     * 注单次数
     */
    private BigDecimal betOrderNum;

    /**
     * 纯投注额
     */
    private BigDecimal betAmountPay;

    /**
     * 纯投注额
     */
    private BigDecimal betAmount;

    /**
     * 数据源提供
     */
    private String thirdOddsFieldSourceId;

}
