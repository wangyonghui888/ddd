package com.panda.sport.rcs.bean;

import lombok.Data;

import java.util.Date;

/**
    * 赛事事件信息表
    */
@Data
public class RcsMatchEventTypeInfo {
    /**
    * id
    */
    private Long id;

    /**
    * 运动种类id
    */
    private Integer sportId;

    /**
    * 数据源编码
    */
    private String dataSourceCode;

    /**
    * 事件编码
    */
    private String eventCode;

    /**
    * 事件类型  0其它事件 1 安全事件 2 危险事件 3封盘事件 4拒单事件
    */
    private Integer eventType;

    /**
    * 事件描述
    */
    private String eventDescription;

    /**
    * 事件备注
    */
    private String eventRemark;

    /**
    * 事件中文名称
    */
    private String eventName;

    /**
     * 事件英文名称
     */
    private String eventEnName;

    private Long createTime;

    private Long modifyTime;

    private Date updateTime;
}