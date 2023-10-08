package com.panda.sport.data.rcs.dto;

import java.io.Serializable;
import java.util.List;


/**
 * 查询mts限额dto
 */
public class MtsgGetMaxStakeDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 所有串关信息集合
     */
    private List<ExtendBean> extendBeanList;

    /**
     * N串M 中的N
     */
    private Integer n;

    /**
     * false表示计算 n串1    true表示计算n串m
     */
    private Boolean flag;

    public MtsgGetMaxStakeDTO(List<ExtendBean> extendBeanList, Integer n, boolean flag) {
        this.extendBeanList = extendBeanList;
        this.n = n;
        this.flag = flag;
    }

}
