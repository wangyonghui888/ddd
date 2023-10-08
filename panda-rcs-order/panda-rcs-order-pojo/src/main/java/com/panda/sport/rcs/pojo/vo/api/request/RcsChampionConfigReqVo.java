package com.panda.sport.rcs.pojo.vo.api.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RcsChampionConfigReqVo {
    /**
     * 赛事ID
     */
    private String matchId;
    /**
     * 玩法ID
     */
    private Integer playId;
    /**
     * 盘口id
     */
    private String marketId;
}
