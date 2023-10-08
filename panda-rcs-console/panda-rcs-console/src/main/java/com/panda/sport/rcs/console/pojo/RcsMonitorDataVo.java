package com.panda.sport.rcs.console.pojo;

import lombok.Data;

@Data

public class RcsMonitorDataVo {

    private String monitorCode;
    private String createTimeHours;
    private Float allCount;
    private Float value100;
    private Float value200;
    private Float value500;
    private Float value1000;
    private Float value2000;
}
