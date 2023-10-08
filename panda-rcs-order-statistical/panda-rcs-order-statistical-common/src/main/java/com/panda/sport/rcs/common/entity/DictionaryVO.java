package com.panda.sport.rcs.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DictionaryVO implements Serializable {

    private String parentName;

    /**
     * 字典名称
     */
    private String dicName;
    /**
     * 字典值
     */
    private Integer dicValue;
}
