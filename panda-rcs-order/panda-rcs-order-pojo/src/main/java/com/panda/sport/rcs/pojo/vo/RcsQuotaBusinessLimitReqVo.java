package com.panda.sport.rcs.pojo.vo;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 商户限额入参
 * @Author : Paca
 * @Date : 2022-04-30 10:38
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsQuotaBusinessLimitReqVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前是第几页
     */
    private Integer current;

    /**
     * 每一页大小
     */
    private Integer size;

    /**
     * 商户编码
     */
    private List<String> businessCodes;

    /**
     * 0-旧模式，1-新模式，其它-所有
     */
    private Integer straySwitchVal;

    /**
     * 商户单日限额开关，0-关，1-开，其它-所有
     */
    private Integer businessSingleDayLimitSwitch;

    /**
     * 商户单场限额比例
     */
    private BigDecimal businessSingleDayGameProportion;

    /**
     * 行情等级id-pc
     */
    private String tagMarketLevelIdPc;

    /**
     * 行情等级id-其他
     */
    private String tagMarketLevelId;

    /**
     * 商户货量百分比
     */
    private String businessBetPercent;

    /**
     * 限额模式 1普通模式  2信用限额
     */
    private Integer limitType;

    public Integer getCurrent() {
        if (this.current == null) {
            return 1;
        }
        return this.current;
    }

    public Integer getSize() {
        if (this.size == null) {
            return 10;
        }
        return this.size;
    }

    /**
     * 商户渠道code
     */
    private List<String> parentNames;



    private String sportIds;


    /**
     * 用户单关单注限额
     */
    private BigDecimal userQuotaBetRatioLow;


    /**
     * 用户单关单注限额
     */
    private BigDecimal userQuotaBetRatioHigh;


    /**
     * 用户串关单注限额
     */
    private BigDecimal userSingleStrayLimitLow;


    /**
     * 用户串关单注限额
     */
    private BigDecimal userSingleStrayLimitHigh;




    /**
     *电竞货量百分比
     * */
    private BigDecimal gamingBetPercent;


    /**
     * 延迟秒数
     */
    private Integer delay;

    /**
     * 商户风控开关 0关 1开
     */
    private Integer riskStatus;

    /**
     * 商户单日限额比例   0.0001-10
     */
    private BigDecimal businessSingleDayLimitProportion;


    /**
     * 商户单日串关限额比例   0.0001-10
     */
    private BigDecimal businessSingleDaySeriesLimitProportion;


    /**
     * 用户单关累计限额  0.0001-10
     */
    private BigDecimal userQuotaRatio;

    /**
     * 用户单关单注限额
     */
    private BigDecimal userQuotaBetRatio;


    /**
     * 用户串关限额比例   0.0001-10
     */
    private BigDecimal userStrayQuotaRatio;

    /**
     * 用户串关单场限额
     */
    private Long userSingleStrayLimit;

    /**
     * 冠军玩法用户限额比例   0.0001-10
     */
    private BigDecimal championUserProportion;

    /**
     * 冠军玩法商户限额比例   0.0001-10
     */
    private BigDecimal championBusinessProportion;

    /**
     * 赔率分组动态风控开关 0关 1开
     */
    private Integer tagMarketLevelStatus;
    /**
     * 投注货量动态风控 0：关  1：开
     */
    private Integer betVolumeStatus;


    /**
     * 赛种集合
     */
    private List<Integer> sportIdList;


    /**
     * 业务ids
     */
    private List<Long> businessLimitIds;

    private Integer checkSelect;











}
