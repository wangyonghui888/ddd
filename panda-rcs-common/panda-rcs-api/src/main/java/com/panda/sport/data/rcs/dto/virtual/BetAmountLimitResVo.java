package com.panda.sport.data.rcs.dto.virtual;

import lombok.Data;

/**
 * 获取虚拟赛事投注限额 返回VO
 *
 * @description:
 * @author: lithan
 * @date: 2020-12-29 20:33:36
 */
@Data
public class BetAmountLimitResVo implements java.io.Serializable {
    /**
     * 最大投注额度
     */
    private Double maxStake;
    /**
     * 最小投注额度
     */
    private Double minStake;

    /**
     * 串关类型
     */
    Integer seriesType;
}