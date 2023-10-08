package com.panda.sport.rcs.pojo.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.utils.LongToStringSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 投注项/坑位-期望值/货量
 * </p>
 *
 * @author author
 * @since 2021-02-19
 */
@Data
public class RcsPredictBetOddsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 体育种类
     */
    private Long sportId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Integer playId;

    /**
     * 类型  1投注项 2坑位
     */
    private Integer dataType;

    /**
     * 盘口ID /坑位ID
     */
    @JSONField(serializeUsing = LongToStringSerializer.class)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dataTypeValue;

    /**
     * 投注项标识
     */
    private String oddsType;

    /**
     * 注单次数（投注项）
     */
    private BigDecimal betOrderNum;

    /**
     * 货量-纯投注额
     */
    private BigDecimal betAmount;

    /**
     * 货量-纯赔付额
     */
    private BigDecimal betAmountPay;

    /**
     * 货量-混合型（注单亚赔大于1的，货量为注单额*亚赔；注单亚赔小于或等于1的，货量为注单额）
     */
    private BigDecimal betAmountComplex;

    /**
     * 期望 盈利值(不包含本金)
     */
    private BigDecimal profitValue;

    /**
     * 联赛id
     */
    private Long standardTournamentId;

    /**
     * 修改时间
     */
    private Long modifyTime;

    /**
     * 主客 home 主 away 客
     */
    private String isHome;

    /**
     * 1.早盘  2.滚球
     */
    private Integer matchType;

    /**
     * 最大赔付金额
     */
    private BigDecimal paidAmount;


    /**
     * 投注项排序
     */
    private Integer orderOdds;

    /**
     * 子玩法ID
     */
    private String subPlayId;

    /**
     * 注单次数（投注项）进球后清零
     */
    private BigDecimal betOrderNumTemp;

    /**
     * 货量-纯投注额(进球后清零)
     */
    private BigDecimal betAmountTemp;

    /**
     * 货量-纯赔付额 进球后清零
     */
    private BigDecimal betAmountPayTemp;

    /**
     * 货量-混合型（注单亚赔大于1的，货量为注单额*亚赔；注单亚赔小于或等于1的，货量为注单额）
     * 进球后清零
     */
    private BigDecimal betAmountComplexTemp;
    /**
     * 单关/串关 1单关 2串关
     */
    private Integer seriesType;
}
