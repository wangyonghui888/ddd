package com.panda.sport.rcs.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ErrorMessagePrompt implements Serializable {
    private String hintMsg;
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
    private boolean paInfo;
}
