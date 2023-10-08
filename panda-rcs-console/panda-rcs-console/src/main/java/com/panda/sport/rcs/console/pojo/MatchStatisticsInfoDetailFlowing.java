package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author :  vector 2.3
 * @Project Name :  data-realtime
 * @Package Name :  com.panda.sport.data.realtime.api.message
 * @Description :  TODO
 * @Date: 2019-10-07 17:10
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class MatchStatisticsInfoDetailFlowing implements Serializable{
    /**
     * 表ID, 自增
     */
    private Long id;

    /**
     * 链路id
     */
    private String linkId;

    /**
     * 原本id
     */
    private Long oId;

    /**
     * 标准赛事id
     */
    private Long standardMatchId;

    private Long matchStatisticsInfoId;

    private String code;

    private Integer firstNum;

    private Integer secondNum;

    private Integer t1;

    private Integer t2;

    private Long createTime;

    private Long modifyTime;

    private String insertTime;


}
