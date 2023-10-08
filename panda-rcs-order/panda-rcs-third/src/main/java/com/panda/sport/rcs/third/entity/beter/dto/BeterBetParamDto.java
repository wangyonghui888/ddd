package com.panda.sport.rcs.third.entity.beter.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Beulah
 * @date 2023/3/21 11:43
 * @description 外部请求传参
 */

@Data
public class BeterBetParamDto implements Serializable {

    private static final long serialVersionUID = 1L;

    //单关投注项
    private List<BeterThirdParamDto> bets;

    //串关投注项
    private List<BeterThirdParamBaseDto> seriesBets;

    private Integer seriesType;

    //投注金额
    private BigDecimal amount;

    //用户id
    private String userId;

    //注单id
    private String orderNo;

    //币种
    private String currency;

    //语言
    private String locale;

    //是否接收赔率变更
    private boolean acceptChanges;


}
