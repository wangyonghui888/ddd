package com.panda.sport.rcs.limit.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 单关用户可用额度出参
 * @Author : Paca
 * @Date : 2021-12-17 11:57
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class StrayUserSeriesLimitResVo implements Serializable {
    /**
     * 串关类型  2 表示 2串1 3表示 3串N
     */
    private Integer seriesType;
    /**
     * 单日串关可用额度
     */
    private BigDecimal dailyStrayLimit;

}
