package com.panda.sport.rcs.console.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author :  toney
 * @Project Name :  rcs-parent
 * @Package Name :  com.panda.sport.rcs.pojo.tourTemplate
 * @Description :  事件/结算审核时间
 * @Date: 2020-05-12 16:41
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class RcsTournamentTemplateEvent implements Serializable {
    private Long id;

    /**
     * 模板id
     */
    private Long templateId;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 事件描述
     */
    private String eventDesc;

    /**
     * 审核时间
     */
    private Integer eventHandleTime;

    /**
     *结算时间
     */
    private Integer settleHandleTime;

    /**
     * 排序
     */
    private Integer sortNo;

    /**
     * 创建时间
     */
    @JsonSerialize(using = DateFormatSerializer.class)
    private Date createTime;

    /**
     * 更新时间
     */
     @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;

    /**
     * 操作類別編碼
     */
    private Integer operatePageCode;

    /**
     * 異動前資料
     */
    private RcsTournamentTemplateEvent beforeParams;

    /**
     * 赛事id
     */
    private Long matchId;

    /**
     * 球队
     */
    //private List<MatchTeamInfo> teamList;

    /**
     *  赛事管理id
     */
    private String matchManageId;
}
