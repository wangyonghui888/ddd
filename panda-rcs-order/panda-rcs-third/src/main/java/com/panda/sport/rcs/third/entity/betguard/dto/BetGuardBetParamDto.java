package com.panda.sport.rcs.third.entity.betguard.dto;

import cn.hutool.core.date.DateTime;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Beulah
 * @date 2023/3/21 11:43
 * @description 外部请求传参
 */

@ToString
@Builder
public class BetGuardBetParamDto implements Serializable {

    private static final long serialVersionUID = 1L;

    //令牌
    private String authToken;
    //主动唯一号
    private Long   transactionId;
    //注单id
    private Long betId;
    //金额
    private BigDecimal amount;
    //创建时间
    private DateTime created;
    //注单类型
    private int betType;
    //赔率喜好 0不变动 1接受更好 2任意
    private int acceptTypeId;
    //(选填)
    private int systemMinCount;
    //总金额
    private BigDecimal totalPrice;
    //注单状态 (Accepted = 1, Returned = 2, Lost = 3, Won = 4, CashedOut = 5)
    private int state;
    //是否滚球
    private Boolean isLive;
    //币种
    private String currency;
    //客户端id
    private int clientId;
    //可能盈利金额
    private BigDecimal possibleWin;
    //投注项信息
    private List<BetGuardSelectionBase> selections;
    //(Decimal = 0, Fractional = 1, American = 2, HongKong = 3, Malay = 4, Indo = 5) (选填)
    private int oddType;
    //是否是eachWay投注(选填)
    private Boolean isEachWay;


}
