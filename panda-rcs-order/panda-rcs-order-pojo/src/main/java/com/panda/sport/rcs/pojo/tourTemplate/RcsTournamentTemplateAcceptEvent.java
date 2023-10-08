package com.panda.sport.rcs.pojo.tourTemplate;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;

/**
 * rcs_tournament_template
 * 触发订单延迟的危险事件及延迟时间
 * @author toney
 */
@Data
public class RcsTournamentTemplateAcceptEvent implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 模板id
     */
    private Long templateId;

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

    /**
     * 排序id
     */
    private Integer sortNo;

    private Integer status;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date crtTime;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public RcsTournamentTemplateAcceptEvent(String eventCode, String eventDesc, Integer delayTime, Integer sortNo, Integer status) {
        this.eventCode = eventCode;
        this.eventDesc = eventDesc;
        this.delayTime = delayTime;
        this.sortNo = sortNo;
        this.status = status;
    }

    public RcsTournamentTemplateAcceptEvent() {
    }
}