package com.panda.sport.rcs.console.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class RcsLogFomat {
    /**
     * 主键
     */
    private Long id;

    private String logType;

    private String oldVal;

    private String uid;

    private String logDesc;

    private String dynamicBean;

    private String name;

    private String logId;

    private String newVal;

    private String createTime;

    private String ctrTime;
}