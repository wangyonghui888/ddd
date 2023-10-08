package com.panda.sport.rcs.pojo.vo.api.request;

import com.panda.sport.rcs.log.annotion.format.LogFormatAnnotion;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsChampionConfigUpdateReqVo {
    /**
     * 修改类型（1.跳水注额，跳水概率变化 2.限额，用户单注赔付限额）
     */
    private Integer updateType;
    /**
     * 限额类型（1.商户玩法 2.用户玩法 3.用户单注 4.用户单项） 和 rcs_champion_trade_config表中的type字段对应
     */
    private Integer limitType;
    /**
     * 投注项ID（limitType=4的时候时有值）
     */
    private String oddsType;
    /**
     * 投注项ID
     */
    private String oddsFiedsId;
    /**
     * 盘口id
     */
    private Long marketId;
    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 玩法id
     */
    private Long playId;
    /**
     * 额度
     */
    private BigDecimal amount;
    /**
     * 单项累计跳水注额
     */
    private BigDecimal oneTotalOddsAmount;
    /**
     * 单枪跳水注额
     */
    private BigDecimal oneOddsAmount;
    /**
     * 一次跳水概率
     */
    private BigDecimal oneProbability;
    /**
     * 二次跳水概率
     */
    private BigDecimal twoProbability;
    /**
     * 三次跳水概率
     */
    private BigDecimal threeProbability;
}
