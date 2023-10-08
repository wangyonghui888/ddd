package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class RcsMonitorData {
    private Long id;

    private String uuid;

    /**
    * 服务名
    */
    private String serviceName;

    /**
    * 监视类型
    */
    private String monitorType;

    /**
    * 监视码
    */
    private String monitorCode;

    private Integer mainType;

    /**
    * 日期
    */
    private Date mainDateStr;

    /**
    * 处理类
    */
    private String handleClass;

    private String extMap;

    private String createTimeHours;
    /**
    * 执行时间
    */
    private Integer exeTime;

    private Date createTime;
}