package com.panda.rcs.logService.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;

import java.util.Date;

/**
 * rcs_tournament_template
 * 触发订单延迟的危险事件及延迟时间
 *
 * @author toney
 */
@Data
public class RcsTournamentTemplateAcceptEvent  {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 接拒数据源id
     */
    private Long acceptConfigId;
    /**
     * 模板id
     */
    @TableField(exist = false)
    private Long templateId;

    /**
     * 玩法集id
     */
    @TableField(exist = false)
    private Integer categorySetId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件id
     */
    private String eventId;

    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 排序id
     */
    private Integer sortNo;

    /**
     * 状态：1：选中  0：没有选中
     */
    private Integer status;


    /**赛事种类*/
    @TableField(exist = false)
    private Integer sportId;

    /**赛事id*/
    @TableField(exist = false)
    private Long matchId;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date createTime;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;
}