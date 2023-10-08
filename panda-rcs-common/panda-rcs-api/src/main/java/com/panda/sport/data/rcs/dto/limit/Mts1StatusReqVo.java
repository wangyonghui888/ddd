package com.panda.sport.data.rcs.dto.limit;

import lombok.Data;

/**
 *  获取mts-1开关配置
 *
 * @description:
 * @author: lithan
 * @date: 2023-02-15 11:06:40
 */
@Data
public class Mts1StatusReqVo implements java.io.Serializable {


    /**
     * 赛事ID
     */
    private Long matchId;

    /**
     * 1：早盘；0：滚球
     */
    Integer matchType;

}