package com.panda.sport.rcs.vo;

import lombok.Data;

/**
 * @author :  myname
 * @Project Name :  xindaima
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  TODO
 * @Date: 2020-07-07 16:59
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class OrderSummaryVo {
    /**
     * 投注项id
     */
    private Long playOptionsId;
    /**
     * 赔率
     */
    private Double oddsValue;
    /**
     * 下注金额
     */
    private Long betAmount;
    /**
     * 体育种类
     */
    private Long sportId;
    /**
     * 赛事id
     */
    private Long matchId;
    /**
     * 玩法id
     */
    private Long playId;
    /**
     * 盘口Id
     */
    private Long marketId;

}
