package com.panda.rcs.order.reject.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.sport.rcs.utils.DateFormatSerializer;
import lombok.Data;

import java.util.Date;

/**
 * 接距配置类
 */
@Data
public class RcsTemplateEventInfoConfig {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 玩法集id
     */
    private Integer categorySetId;
    /**
     * 1.常规接距 2.提前结算接距
     */
    private Integer rejectType;
    /**
     * 事件类型
     */
    private String eventType;


    /**
     * 事件编码
     */
    private String eventCode;

    /**
     * 事件名称
     */
    private String eventName;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date createTime;

    @JsonSerialize(using = DateFormatSerializer.class)
    private Date updateTime;

}