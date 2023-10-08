package com.panda.rcs.push.entity.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class CustomizedEventBean implements Serializable {

    /**
     * 事件code
     */
    private String eventCode;

    /**
     * 事件名
     */
    private String eventName;

    /**
     * 事件名(英文)
     */
    private String eventEnName;

    /**
     * 事件类型  0其它事件 1 安全事件 2 危险事件 3封盘事件 4拒单事件
     */
    private Integer eventType;

    /**
     * 体育种类
     */
    private Long sportId;

    /**
     * home 主 away 客
     */
    private String homeAway;

    /**
     * 当前时间
     */
    private Integer currentTime;

    /**
     * 当前比分
     */
    private String score;

    /**
     * 对应data_source.code
     */
    private String dataSourceCode;
    /**
     * 事件时间
     */
    private Long eventTime;

    /**
     * 标准赛事id
     */
    private Long standardMatchId;
    /**
     * 当前局比分
     */
    private String setScore;
    /**
     * 当前小分
     */
    private String currentScore;

    /**
     * 备注
     */
    private String remark;

}
