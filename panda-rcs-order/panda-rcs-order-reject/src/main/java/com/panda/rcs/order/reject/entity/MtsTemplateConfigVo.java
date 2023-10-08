package com.panda.rcs.order.reject.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class MtsTemplateConfigVo implements Serializable {
    private static final long serialVersionUID = -3933529427846823417L;
    //mts接距开关
    private Integer mtsSwitch;
    //接距百分比
    private BigDecimal contactPercentage;
    //滚球延时时间
    private Integer waitTime;
}
