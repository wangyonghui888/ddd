package com.panda.sport.data.rcs.dto.bts;

import com.panda.sport.data.rcs.dto.ExtendBean;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Beulah
 * @date 2023/3/21 11:43
 * @description 外部请求传参
 */

@Data
public class ThirdBetParamDto implements Serializable {

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

    /**
     * 三方数据商标识
     */
    private String third;

    public ThirdBetParamDto(List<ExtendBean> extendBeanList, Integer n, boolean flag, String third) {
        this.extendBeanList = extendBeanList;
        this.n = n;
        this.flag = flag;
        this.third = third;
    }

}
