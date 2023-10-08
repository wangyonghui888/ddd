package com.panda.sport.rcs.predict.vo;

import lombok.Data;

import java.util.List;

@Data
public class RcsPredictOddsPlaceNumMqDelayVo extends RcsPredictOddsPlaceNumMqVo {


    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 注单号
     */
    private String betNo;

    /**
     * key
     */
    private String delayKey;
}
