package com.panda.sport.rcs.vo;

import lombok.Data;

/**
 * @author :  wealth
 * @Project Name :  panda-rcs-trade-service
 * @Package Name :  com.panda.sport.rcs.vo.ClearBalanceVO
 * @Description :  平衡值清理入参
 * @Date: 2023-02-27 15:30
 * @ModificationHistory
 * --------  ---------  --------------------------
 */
@Data
public class ClearBalanceVO {

    /**
     * 球种ID
     */
    private Long sportId;

    /**
     * 盘口ID
     */
    private Long marketId;

    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 玩法ID
     */
    private Long playId;

    /**
     * 子玩法ID
     */
    private Long subPlayId;

    /**
     * 盘口位置
     */
    private Integer placeNum;

    /**
     * 清理类型
     */
    private Integer clearType;

    /**
     * 预计时间
     */
    private String dateExpect;

    /**
     * 投注项类型
     */
    private String oddsType;
}
