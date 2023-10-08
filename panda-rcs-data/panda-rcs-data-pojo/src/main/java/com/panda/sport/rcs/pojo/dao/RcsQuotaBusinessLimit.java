package com.panda.sport.rcs.pojo.dao;

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
 * @Date: 2020-09-03 17:31
 * 商户限额
 */
@Data
public class RcsQuotaBusinessLimit extends RcsBaseEntity<RcsQuotaBusinessLimit> {
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
     * 	商户单日串关限额比例   0.0001-10
     */
    private BigDecimal businessSingleDaySeriesLimitProportion;
    /**
     *	 商户单日串关限额
     */
    private Long businessSingleDaySeriesLimit;
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
    /**
     * 行情等级  赔率等级
     */
    private String tagMarketLevelId;

    /**
     * 行情等级id-pc
     */
    private String tagMarketLevelIdPc;
    
    /**
     * 商户货量百分比
     */
    private BigDecimal businessBetPercent;

    /**
     * 用户串关单场限额
     */
    private Long userSingleStrayLimit;

    /**
     * 用户串关限额比例   0.0001-10
     */
    private BigDecimal userStrayQuotaRatio;

    /**
     * 用户单关单注限额
     */
    private BigDecimal userQuotaBetRatio;

    /**
     * 冠军玩法商户限额比例
     */
    private BigDecimal championBusinessProportion;

    /**
     * 冠军玩法用户限额比例
     */
    private BigDecimal championUserProportion;

    /**
     * 商户所属渠道商code
     */
    private String parentName;

    /**
     * 串关新版限额开关
     */
    private Integer straySwitchVal;
}
