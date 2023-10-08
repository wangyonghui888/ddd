package com.panda.sport.rcs.pojo;

import lombok.Data;

@Data
public class MatchStatisticsInfoDetail {
    private Long id;

    /**
    * 标准赛事id
    */
    private Long standardMatchId;

    //不再使用
//    private Long matchStatisticsInfoId;

    private String code;

    private Integer firstNum;

    private Integer secondNum;

    private Integer t1;

    private Integer t2;

    private Long createTime;

    private Long modifyTime;
}