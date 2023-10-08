package com.panda.sport.rcs.predict.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * forecast 足球 玩法 范围
 * </p>
 *
 * @author author lithan
 * @since 2021-12-10
 */
@Data
public class ForecastScopeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 最小
     */
    private Integer min;

    /**
     * 最大
     */
    private Integer max;

}
