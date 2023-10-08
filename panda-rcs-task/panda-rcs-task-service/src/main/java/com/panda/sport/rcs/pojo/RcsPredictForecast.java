package com.panda.sport.rcs.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.panda.sport.data.rcs.dto.OrderItem;
import com.panda.sport.rcs.core.bean.RcsBaseEntity;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 预测forecast表
 * </p>
 *
 * @author lithan auto
 * @since 2020-07-18
 */
@Data
public class RcsPredictForecast extends RcsBaseEntity<RcsPredictForecast> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 运动种类
     */
    private Integer sportId;

    /**
     * 标准赛事id
     */
    private Long matchId;

    /**
     * 赛事类型:1赛前,2滚球
     */
    private Integer matchType;

    /**
     * 玩法id
     */
    private Integer playId;

    /**
     * 盘口id
     */
    private Long marketId;

    /**
     * 投注项
     */
    private String oddsItem;

    /**
     * 基准分 投注时比分
     */
    private String betScore;

    /**
     * 完整让球(盘口值)
     */
    private String marketValueComplete;

    /**
     * 当前让球(盘口值)
     */
    private String marketValueCurrent;

    /**
     * forecast预测分数(1.大小玩法时,表示两队进球和. 2.让球玩法时,表示进球数量差值)
     */
    private Integer forecastScore;

    /**
     * 预测盈利(庄家视角)
     */
    private BigDecimal profitAmount;

    /**
     * 创建时间
     */
    private Long createTime;

    public static RcsPredictForecast buildByOrderItem(OrderItem item) {
        RcsPredictForecast rcsProfitRectangle = new RcsPredictForecast();
        rcsProfitRectangle.setSportId(item.getSportId());
        rcsProfitRectangle.setMatchId(item.getMatchId());
        rcsProfitRectangle.setMatchType(item.getMatchType());
        rcsProfitRectangle.setPlayId(item.getPlayId());
        rcsProfitRectangle.setMarketId(item.getMarketId());
        rcsProfitRectangle.setOddsItem(item.getPlayOptions());
        rcsProfitRectangle.setBetScore(item.getScoreBenchmark());
        rcsProfitRectangle.setCreateTime(System.currentTimeMillis());
        rcsProfitRectangle.setMarketValueComplete(item.getMarketValueNew());
        rcsProfitRectangle.setMarketValueCurrent(item.getMarketValue());
        return rcsProfitRectangle;
    }
}
