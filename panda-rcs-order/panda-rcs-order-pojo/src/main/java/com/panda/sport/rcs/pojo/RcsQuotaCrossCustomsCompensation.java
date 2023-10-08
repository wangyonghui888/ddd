package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2020-09-04 10:29
 * @ModificationHistory Who    When    What
 * 商户限额
 */
@Data
public class RcsQuotaCrossCustomsCompensation extends RcsBaseEntity<RcsQuotaCrossCustomsCompensation> {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 商户id
     */
    private String businessId;
    /**
     * 商户名称
     */
    private String businessName;
    /**
     * 商户单日限额比例   0.0001-10
     */
    private BigDecimal businessSingleDayLimitProportion;
    /**
     * 商户单日限额
     */
    private Long businessSingleDayLimit;
    /**
     * 商户单场限额比例   0.0001-10
     */
    private BigDecimal businessSingleDayGameProportion;
    /**
     * 用户限额比例   0.0001-10
     */
    private BigDecimal userQuotaRatio;
    /**
     * 当日状态 1正常  2危险  3高位
     */
    private Integer statusOfTheDay;
    /**
     * 状态 0未生效  1生效
     */
    private Integer status;
}
