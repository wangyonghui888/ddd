package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 清理Bean子类
 */
@Data
public class ClearSubDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
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
    private Long marketId;

    /**
     * 投注项类型
     */
    private String oddsType;

    /**
     * 投注项id
     */
    private Long marketOddsId;

    /**
     * 盘口位置
     */
    private Integer placeNum;


    private Integer type;

}
