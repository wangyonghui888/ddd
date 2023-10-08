package com.panda.sport.rcs.third.entity.gts;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class GtsBetReceiverCache implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 投注编号
     */
    private String id ;
    /**
     * 平台名
     */
    private String bookmakerName;

    /**
     * 用户 玩家 id
     */
    private String playerId;

    /**
     * 投注金额 我们只会有整数
     */
    private BigDecimal totalStake;


    /**
     * 币种标识
     */
    private String currencyCode;

    /**
     * 币种标识
     */
    private String systemBetType;


    /**
     * 是否优先 1 优先高 2优先低
     */
    private Integer priority;

    /**
     * 投注项
     */
    private List<GtsBetReceiveLegsVo> legs;

    /**
     * zhe k
     */
    private BigDecimal disAmount;


    /**
     * 投注项id映射
     */
    private Map<String,String> playOptionMap = new HashMap<>();

    /**
     * 下注时间
     */
    private String betPlacedTimestampUTC;





}
