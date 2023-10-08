package com.panda.sport.rcs.pojo.config;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * @Project Name : rcs-parent
 * @Package Name : rcs-parent
 * @Description : 构建盘口配置
 * @Author : Paca
 * @Date : 2021-03-14 15:07
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class BuildMarketConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Long playId;

    /**
     * 0-滚球，1-赛前
     */
    private Integer matchType;

    /**
     * MY-马来盘，EU-欧洲盘，Other-其他
     */
    private String marketType;

    /**
     * 盘口差
     */
    private BigDecimal marketHeadGap;

    /**
     * 最大盘口数
     */
    private Integer marketCount;

    /**
     * 相邻盘口差值
     */
    private BigDecimal marketNearDiff;

    /**
     * 相邻盘口赔率差值
     */
    private BigDecimal marketNearOddsDiff;

    /**
     * 盘口调整幅度
     */
    private BigDecimal marketAdjustRange;

    /**
     * 位置spread
     */
    private Map<Integer, BigDecimal> placeSpreadMap;

    /**
     * 玩法水差
     */
    @Deprecated
    private BigDecimal playWaterDiff;

    /**
     * 位置水差
     */
    private Map<Integer, BigDecimal> placeWaterDiffMap;

    /**
     * 清水差标志
     */
    @Deprecated
    private boolean clearFlag = false;

    @Deprecated
    public BigDecimal getPlayWaterDiff() {
        return BigDecimal.ZERO;
    }

    public BigDecimal getMarketHeadGap() {
        if (marketHeadGap == null) {
            return BigDecimal.ZERO;
        }
        return marketHeadGap;
    }
}
