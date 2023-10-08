package com.panda.sport.rcs.trade.param;

import groovy.transform.EqualsAndHashCode;
import lombok.Data;

/**
 * @author :  pumelo
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.trade.vo.tourTemplate
 * @Description :  联赛状态参数
 * @Date: 2020-05-12 19:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TournamentStatusParam {


    /**
     * 赛事id
     */
    private Long matchId;

    /**
     * 1：早盘；0：滚球
     */
    private Integer matchType;


    /**
     * 赛事提前结算开关 0:关 1:开
     */
    private Integer matchPreStatus;

}
