package com.panda.sport.rcs.pojo.vo;

import lombok.Data;

/**
 * ws比分实例
 */
@Data
public class WsScoreVO  {

    private Integer period;
    /**
     * 盘
     */
    private Integer firstNum;
    /**
     * 局
     */
    private Integer secondNum;
    /**
     * 网球：盘比分
     */
    private String matchScore;
    /**
     * 网球：抢分
     */
    private String qiangScore;
    /**
     * 网球：小分
     */
    private String currentScore;
    /**
     * 网球：小分
     */
    private String setScore;
}