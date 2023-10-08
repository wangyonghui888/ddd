package com.panda.sport.rcs.pojo.tourTemplate;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo.tourTemplate
 * @Description :  联赛模板玩法与margain之间引用
 * @Date: 2020-05-12 16:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsTournamentTemplatePlayMargainRefHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * margain表id
     */
    private Long margainId;
    /**
     * 时间轴值
     */
    private Long timeVal;
    /**
     * margain值
     */
    private String margain;
    /**
     * 0 投注额差值 1 投注额/赔付混合差值
     */
    private Integer balanceOption;
    /**
     * 最大赔率
     */
    private BigDecimal maxOdds;
    /**
     * 最小赔率
     */
    private BigDecimal minOdds;
    /**
     * 跳赔规则
     * 0 累计/单枪跳分 1 累计差值跳分
     */
    private Integer oddChangeRule;
    /**
     * 上盘累计限额
     */
    private Long homeMultiMaxAmount;
    /**
     * 上盘单枪限额
     */
    private Long homeSingleMaxAmount;
    /**
     * 上盘单枪赔率变化率
     */
    private BigDecimal homeSingleOddsRate;
    /**
     * 上盘累计赔率变化率
     */
    private BigDecimal homeMultiOddsRate;

    /**
     * 下盘单枪赔率变化率
     */
    private BigDecimal awaySingleOddsRate;

    /**
     * 下盘累计赔率变化率
     */
    private BigDecimal awayMultiOddsRate;
    /**
     * 单注投注/赔付限额
     */
    private Long orderSinglePayVal;
    /**
     * 单注投注限额
     */
    private Long orderSingleBetVal;
    /**
     *预约赔付累计限额
     */
    private Long pendingOrderPayVal;
    /**
     * 用户累计赔付限额
     */
    private Long userMultiPayVal;
    /**
     * 累计差值
     */
    private Long multiDiffVal;
    /**
     * 累计概率变化
     */
    private BigDecimal multiOddsRate;
    /**
     * 上盘一级限额
     */
    private Long homeLevelFirstMaxAmount;
    /**
     * 上盘二级限额
     */
    private Long homeLevelSecondMaxAmount;
    /**
     * 上盘一级赔率变化率
     */
    private BigDecimal homeLevelFirstOddsRate;
    /**
     * 上盘二级赔率变化率
     */
    private BigDecimal homeLevelSecondOddsRate;
    /**
     * 下盘一级赔率变化率
     */
    private BigDecimal awayLevelFirstOddsRate;
    /**
     * 下盘二级赔率变化率
     */
    private BigDecimal awayLevelSecondOddsRate;
    /**
     * 操盘设置1：勾选   0：未勾选
     */
    private Integer isMarketConfig;
    /**
     * 额度设置1：勾选   0：未勾选
     */
    private Integer isQuotaConfig;
    /**
     * 暂停margin
     */
    private String pauseMargain;
    /**
     * 常规接单等待时间
     */
    private Integer normalWaitTime;
    /**
     * 暂停接单等待时间
     */
    private Integer pauseWaitTime;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date createTime;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;
    
    private String status;
}
