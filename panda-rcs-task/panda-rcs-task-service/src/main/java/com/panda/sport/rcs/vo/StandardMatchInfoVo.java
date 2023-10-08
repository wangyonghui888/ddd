package com.panda.sport.rcs.vo;

import com.panda.sport.rcs.pojo.StandardMatchInfo;
import lombok.Data;

/**
 * @author :  Administrator
 * @Project Name :  panda-rcs-task-group
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  TODO
 * @Date: 2020-08-02 10:30
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StandardMatchInfoVo extends StandardMatchInfo {

    /**
     * 滚球操盘手id
     */
    private String liveTraderId;

    /**
     * 滚球操盘手
     */
    private String liveTrader;

    /**
     * 赛前操盘手id
     */
    private String preTraderId;

    /**
     * 赛前操盘手
     */
    private String preTrader;

    /**
     * 早盘盘口数
     */
    private Integer marketCount;

    /**
     * 滚球盘口数
     */
    private Integer liveMarketCount;

    /**
     * 赛前开售时间
     */
    private Long preMatchTime;

    /**
     * 完赛时间
     */
    private Long  endTime;

    /**
     * 第三方赛事信息
     */
    private String thirdMatchListStr;
}
