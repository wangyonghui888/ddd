package com.panda.sport.rcs.pojo.dto;

import lombok.Data;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.data.dto
 * @Description :  联赛模板接拒单模板
 * @Date: 2020-05-28 17:49
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class TournamentTemplateAcceptEventDto {
    private Integer id;
    /**
     * 事件编码
     */
    private String eventCode;
    /**
     * 描述
     */
    private String eventDesc;
    /**
     * 事件延迟时间，单位秒
     */
    private Integer delayTime;
}
