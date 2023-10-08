package com.panda.sport.rcs.pojo.tourTemplate;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import com.panda.sport.rcs.utils.LongToStringSerializer;
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
     * 事件描述
     */
    private String eventDesc;
    /**
     * 事件时间
     */
    private Integer eventHandleTime;
    /**
     *结算时间
     */
    private Integer settleHandleTime;

    @TableField(exist = false)
    private  Integer status;
    /**
     * 创建时间
     */
    @TableField(exist = false)
    @JsonSerialize(using = DateFormatSerializer.class)
    private Date crtTime;
    /**
     * 更新时间
     */
     @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;

    /**
     * 排序
     */
    private Integer sortNo;


    private static final long serialVersionUID = 1L;


    public RcsTournamentTemplateEvent() {
    }

    public RcsTournamentTemplateEvent(String eventCode, String eventDesc, Integer eventHandleTime, Integer settleHandleTime, Integer status, Integer sortNo) {
        this.eventCode = eventCode;
        this.eventDesc = eventDesc;
        this.eventHandleTime = eventHandleTime;
        this.settleHandleTime = settleHandleTime;
        this.status = status;
        this.sortNo = sortNo;
    }
}
