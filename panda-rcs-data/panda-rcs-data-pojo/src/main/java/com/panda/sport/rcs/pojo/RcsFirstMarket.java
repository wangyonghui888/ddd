package com.panda.sport.rcs.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 赛事玩法初盘表
 */
@Data
public class RcsFirstMarket {
    private Long id;

    /**
     * 标准赛事id
     */
    private Long standardMatchId;

    /**
     * 玩法id
     */
    private Long playId;

    /**
     * 1:初盘 2:赛前终盘
     */
    private Integer type;

    /**
     * 值
     */
    private String value;

    private Date createTime;
}