package com.panda.sport.data.rcs.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OddStatusMessagePrompt implements Serializable {
    /**
     * 异常消息
     */
    private String hintMsg;
    /**
     * 是否mts
     */
    private boolean mtsInfo;
    //事件编码
    private String currentEvent;
    //操作描述编码
    private Integer infoStatus;
    /**
     * 注单编号
     */
    private String betNo;
    /*
     * 是否pa-0
     */
    private Boolean paInfo;

    private Boolean pass;
}
