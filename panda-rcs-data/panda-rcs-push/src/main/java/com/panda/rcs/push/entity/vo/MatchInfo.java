package com.panda.rcs.push.entity.vo;

import lombok.Data;

@Data
public class MatchInfo {

    private String matchId;

    /**
     * 赛事状态
     */
    private Integer matchStatus;

    /**
     * 赛事阶段
     */
    private Integer matchStage;

    /**
     * 创建时间
     */
    private Long createTime;

}
