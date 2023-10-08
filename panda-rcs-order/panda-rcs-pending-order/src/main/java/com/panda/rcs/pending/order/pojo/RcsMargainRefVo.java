package com.panda.rcs.pending.order.pojo;

import lombok.Data;

/**
 * @Describtion
 * @Auther jstyDC
 * @Date 2022-05-2022/5/4 14:21
 */
@Data
public class RcsMargainRefVo {

    /**
     * 预约单场赔付累计限额
     */
    private Long pendingOrderPayVal;
    /**
     * 用户单注赔付限额
     */
    private Long singlePayLimit;
    /**
     * 用户累计赔付限额
     */
    private Long cumulativeCompensationPlaying;

    /**
     * 0 早盘 1滚球
     */
    private Integer matchType;

    /**
     * 玩法id
     */
    private Integer playId;

}
