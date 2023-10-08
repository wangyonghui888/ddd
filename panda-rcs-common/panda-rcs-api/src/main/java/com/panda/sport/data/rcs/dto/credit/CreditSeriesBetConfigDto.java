package com.panda.sport.data.rcs.dto.credit;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 信用串关单注限额
 * @Author : Paca
 * @Date : 2021-07-17 13:04
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class CreditSeriesBetConfigDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 串关类型：
     * <li>2-2串1</li>
     * <li>3-3串N</li>
     * <li>4-4串N</li>
     * <li>5-5串N</li>
     * <li>6-6串N</li>
     * <li>7-7串N</li>
     * <li>8-8串N</li>
     * <li>9-9串N</li>
     * <li>10-10串N</li>
     */
    private Integer seriesType;

    /**
     * 限额值，单位元
     */
    private BigDecimal value;
}
