package com.panda.sport.data.rcs.api;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:异常数据描述信息
 * @date 2019/9/5 23:13
 * @Version 1.0
 */
@Data
public class ExceptionData implements Serializable {
    /**
     * 数据ID
     */
    private String id;
    /**
     * 异常描述
     */
    private String description;

    public ExceptionData() {

    }

    public ExceptionData(String id, String description) {
        this.id = id;
        this.description = description;
    }


}
