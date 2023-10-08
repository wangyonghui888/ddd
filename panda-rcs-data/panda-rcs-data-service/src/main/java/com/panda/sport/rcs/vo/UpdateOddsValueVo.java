package com.panda.sport.rcs.vo;

import lombok.Data;

import java.util.List;

/**
 * @author :  myname
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  TODO
 * @Date: 2020-03-10 16:24
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class UpdateOddsValueVo {
    /**
     * 赛事id
     */
    private Long matchId;

    /**
     * 玩法id
     */
    private Long playId;

    /**
     * 盘口id
     */
    private String marketId;
    /**
     * 投注项数据
     */
    private List<OddsValueVo> oddsValueList;
}
