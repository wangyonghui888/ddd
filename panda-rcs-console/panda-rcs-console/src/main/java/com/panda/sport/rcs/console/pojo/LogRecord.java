package com.panda.sport.rcs.console.pojo;

import lombok.Data;

@Data
public class LogRecord extends BaseBean {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long exeTime;
    private String url;
    private String code;
    private String uuid;
    private String name;
    private String title;
    private String values;
    private String requestVal;
    private String returnVal;
    private String createTime;
    private String userId;
    private String ip;
    private String requestKey;
    private String requestValue;
    private String requestKey1;
    private String requestValue1;
    private String valueType1;
    private String requestKey2;
    private String requestValue2;
    private String valueType2;
    private String requestKey3;
    private String requestValue3;
    private String valueType3;
    private String startTime;
    private String endTime;

}
