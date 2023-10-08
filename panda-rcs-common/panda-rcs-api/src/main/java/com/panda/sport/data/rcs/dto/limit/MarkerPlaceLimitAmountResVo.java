package com.panda.sport.data.rcs.dto.limit;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 获取 玩法盘口位置 限额 返回参数VO
 *
 * @description:
 * @author: lithan
 * @date: 2020-09-15 14:14
 */
@Data
public class MarkerPlaceLimitAmountResVo implements java.io.Serializable{
    /**
     * 赛种
     */
    Integer sportId;
    /**
     * 赛事ID
     */
    Long matchId;
    /**
     * 玩法ID
     */
    Integer playId;
    /**
     * 1：早盘；0：滚球
     */
    Integer matchType;

    /**
     * 盘口位置 1.主盘  2.副盘1 3副盘2 ....
     */
    Integer marketPlaceNum;

    /**
     * 限额
     */
    BigDecimal limitAmount;

    /**
     * 子玩法标识
     */
    private String subPlayId;

}