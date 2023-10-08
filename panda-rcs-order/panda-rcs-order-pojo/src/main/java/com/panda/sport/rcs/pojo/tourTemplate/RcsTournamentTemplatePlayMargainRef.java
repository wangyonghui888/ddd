package com.panda.sport.rcs.pojo.tourTemplate;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author lithan auto
 * @since 2020-09-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsTournamentTemplatePlayMargainRef implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer margainId;

    /**
     * 时间值
     */
    private Integer timeVal;

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
     * 跳赔规则 0 累计/单枪跳分 1 累计差值跳分
     */
    private Integer oddChangeRule;

    /**
     * 累计限额
     */
    private Long homeMultiMaxAmount;

    /**
     * 单枪限额
     */
    private Long homeSingleMaxAmount;

    /**
     * 上盘累计变化率
     */
    private BigDecimal homeMultiOddsRate;

    /**
     * 上盘单枪变化率
     */
    private BigDecimal homeSingleOddsRate;

    /**
     * 下盘累计变化率
     */
    private BigDecimal awayMultiOddsRate;

    /**
     * 下盘单枪变化率
     */
    private BigDecimal awaySingleOddsRate;

    /**
     * 单注投注/赔付限额
     */
    private Long orderSinglePayVal;

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
     * 主一级限额
     */
    private Long homeLevelFirstMaxAmount;

    /**
     * 主二级限额
     */
    private Long homeLevelSecondMaxAmount;

    /**
     * 主一级赔率变化率
     */
    private BigDecimal homeLevelFirstOddsRate;

    /**
     * 主二级赔率变化率
     */
    private BigDecimal homeLevelSecondOddsRate;

    /**
     * 客一级赔率变化率
     */
    private BigDecimal awayLevelFirstOddsRate;

    /**
     * 客二级赔率变化率
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

    private Date createTime;

    private Date updateTime;


}
