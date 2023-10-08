package com.panda.sport.data.rcs.dto.trade;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project Name : panda-rcs-trade-group
 * @Package Name : panda-rcs-trade-group
 * @Description : 用户特殊限额
 * @Author : Paca
 * @Date : 2021-08-18 13:00
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class UserSpecialLimitDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 特殊限额类型，1-无，2-特殊百分比限额
     */
    private Integer specialLimitType;
    
    /**
     * 标签行情等级ID（赔率分组）
     */
    private String tagMarketLevelId;

    /**
     * 	特殊限额百分比
     */
    private BigDecimal percentage;
    
    /**
     * 	投注额外延时
     */
    private Integer betExtraDelay;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;
}
