package com.panda.sport.rcs.third.entity.beter.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Beulah
 * @date 2023/3/20 18:51
 * @description todo
 */
@Data
public class BeterThirdParamDto extends BeterThirdParamBaseDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 投注金额
     */
    private BigDecimal amount;
    /**
     * 币种
     */
    private String currency;
}
