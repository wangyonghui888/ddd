package com.panda.sport.rcs.credit.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Project Name : panda-rcs-order-group
 * @Package Name : panda-rcs-order-group
 * @Description : 串关中每一注注单赔付
 * @Author : Paca
 * @Date : 2021-05-07 15:11
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
@Data
public class SeriesBetPaymentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 注单赔付金额，取混合值
     */
    private Long betPayment;

    /**
     * 注单中赛事下标集合
     */
    private List<Integer> indexList;
}
