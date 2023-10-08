package com.panda.sport.rcs.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wiker
 * @date 2023/8/19 23:06
 **/
@Data
public class RcsMissedOrderConfigStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 商户配置状态是否更改(0未更改,1更改)
     */
    private Integer status;
    /**
     * 商户ID
     */
    private List<Long> merchantIds;
}
