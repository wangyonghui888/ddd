package com.panda.sport.sdk.bean;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.pojo
 * @Description :  TODO
 * @Date: 2020-09-03 17:31
 * 商户限额
 */
@Data
public class RcsQuotaBusinessLimit{

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
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
     * 商户单日串关限额比例   0.0001-10
     */
    private BigDecimal businessSingleDaySeriesLimitProportion;
    /**
     * 商户单日串关限额
     */
    private Long businessSingleDaySeriesLimit;
    /**
     * 商户单日限额开关，0-关，1-开
     */
    private Integer businessSingleDayLimitSwitch;
    /**
     * 商户单日已用限额
     */
    private BigDecimal businessUsedLimit;
    /**
     * 商户单场限额比例   0.0001-10
     */
    private BigDecimal businessSingleDayGameProportion;
    /**
     * 用户单关累计限额  0.0001-10
     */
    private BigDecimal userQuotaRatio;
    /**
     * 用户单关单注限额
     */
    private BigDecimal userQuotaBetRatio;
    /**
     * 信用单注限额比例
     */
    private BigDecimal creditBetRatio;
    /**
     * 当日状态 1正常  2危险  3高位
     */
    private Integer statusOfTheDay;
    /**
     * 状态 0未生效  1生效
     */
    private Integer status;
    /**
     * 代理名称
     */
    private String creditName;
    /**
     * 信用父代理ID，0表示无父代理或普通商户
     */
    private String creditParentAgentId;
    /**
     * 限额模式 1普通模式  2信用限额
     */
    private Integer limitType;

    /**
     * 标签行情开关 1开 0关
     */
    private Integer tagMarketStatus;

    /**
     * 行情等级id-其他
     */
    private String tagMarketLevelId;

    /**
     * 行情等级id-pc
     */
    private String tagMarketLevelIdPc;

    /**
     * 实际盈亏
     */
    private BigDecimal actualProfit;
    /**
     * 是否有代理商户标记
     */
    private Boolean hasSubFlag = false;

    /**
     * 商户货量百分比
     */
    private BigDecimal businessBetPercent;
    /**
     * 冠军玩法商户限额比例   0.0001-10
     */
    private BigDecimal championBusinessProportion;

    /**
     * 冠军玩法用户限额比例   0.0001-10
     */
    private BigDecimal championUserProportion;
    /**
     * 用户串关限额比例   0.0001-10
     */
    private BigDecimal userStrayQuotaRatio;

    /**
     * 商户风控开关 0关 1开
     */
    private Integer riskStatus;

    /**
     * 赔率分组动态风控开关 0关 1开
     */
    private Integer tagMarketLevelStatus;

    /**
     * 串关新版限额开关
     */
    private Integer straySwitchVal;

    /**
     * 用户串关单场限额
     */
    private Long userSingleStrayLimit;


    /**
     * 商户所属渠道商code
     */
    private String parentName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 延时秒数
     */
    private Integer delay;
    /**
     * 赛事种类
     */
    private String sportIds;

    private List<Integer> sportIdList;

}
