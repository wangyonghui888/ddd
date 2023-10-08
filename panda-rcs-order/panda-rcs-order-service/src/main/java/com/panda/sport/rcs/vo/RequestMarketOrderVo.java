package com.panda.sport.rcs.vo;

import lombok.Data;

/**
 * @author :  myname
 * @Project Name :  daima1
 * @Package Name :  com.panda.sport.rcs.vo
 * @Description :  TODO
 * @Date: 2019-12-12 16:37
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RequestMarketOrderVo {
    /**
     * @Description 分页
     **/
    Integer page;
    /**
     * @Description 玩法id
     **/
    private Integer playId;
    /**
     * @Description 盘口id
     **/
    private Long marketId;
    /**
     * @Description 赛事id
     **/
    private Long matchId;
    /**
     * @Description 类型  0滚球界面  1其他页面
     **/
    private Integer type;
}
