package com.panda.sport.rcs.pojo.vo.api.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsChampionConfigResVo {
    /**
     * 玩法ID
     */
    private Integer playId;
    /**
     * 赛事id
     */
    private String matchId;
    /**
     * 盘口id
     */
    private String marketId;
    /**
     * 赛事状态 1 早盘 0 滚球
     */
    private Integer marketType;
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
    /**
     * 商户玩法累计赔付限额
     */
    private BigDecimal merchantPlaysTotalAmount;
    /**
     * 用户玩法累计赔付限额
     */
    private BigDecimal userPlaysAmount;
    /**
     * 用户单注赔付限额
     */
    private BigDecimal userOneAmount;

    /**
     * 状态  0:active 开, 1:suspended 封, 2:deactivated 关, 11:锁 空表示没操作
     */
    private Integer status;

    /**
     * 数据源 0自动 1手动 ,不操作为空
     */
    private Integer dataSource;

    /**
     * 三方盘口数据源状态
     */
    private Integer thirdMarketSourceStatus;
}
