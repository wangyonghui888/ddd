package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

/**
 * 	赛事查询模板名称
 */
@Data
public class TemplateNameForMatchDto {
    /**
     *	赛事id
     */
    private Long matchId;

    /**
     * 1：级别  2：联赛id   3：赛事id
     */
    private Integer type;

    /**
     * 	type为1时的联赛等级
     */
    private Integer levelNum;

    /**
     * 赛事提前结算开关 0:关 1:开
     */
    private Integer matchPreStatus;

    /**
     * 根据type设置（1：设值联赛等级  2：设值联赛id   3：设值赛事id）
     */
    private Long typeVal;
    
    /**
     * 模板名稱
     */
    private String templateName;
}
