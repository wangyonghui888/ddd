package com.panda.sport.data.rcs.dto.limit;

import lombok.Data;

/**
 * 获取 玩法盘口位置 限额 请求参数VO
 *
 * @description:
 * @author: lithan
 * @date: 2020-09-15 14:14
 */
@Data
public class MarkerPlaceLimitAmountReqVo implements java.io.Serializable {

    /**
     * 赛种
     */
    private Integer sportId;
    /**
     * 赛事ID
     */
    private Long matchId;
    /**
     * 玩法ID
     */
    private Integer playId;

    //盘口位置
    private  Integer placeNum;
    //子玩法
    private  String subPlayId;

    /**
     * 1：早盘；0：滚球
     */
    //Integer matchType;

}