package com.panda.rcs.order.entity.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MqMessageVo<T> implements Serializable {

    private T data;

    private Long dataSourceTime;

    private  String linkId;

    private String dataSourceCode;

    private Integer dataType;

    private String tag;

    private String operaterId;

}
