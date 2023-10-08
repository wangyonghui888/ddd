package com.panda.rcs.logService.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author Z9-jing
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessLogVo extends  RcsQuotaBusinessLimitLog{
    private String method;

    private String beforeString;

    private Object[] afterString;
    /**
     * 藏单状态开关  0开 1关
     */
    private Integer status;
    /**
     * 最大藏单金额
     */
    private Long hideMoney;
    private Integer betUserNum;
    private Integer maxAmount;
    private Integer beforeGoalSeconds;
    private String standardTournamentName;
    private String standardMatchName;
    private String standardTeamName;
    private Long goalWarnSetId;

}
