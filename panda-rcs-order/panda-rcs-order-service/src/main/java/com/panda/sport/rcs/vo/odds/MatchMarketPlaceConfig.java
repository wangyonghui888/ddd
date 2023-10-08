package com.panda.sport.rcs.vo.odds;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MatchMarketPlaceConfig {

    private Integer placeNum;

    private String spread;

    private String minOdds;

    private String maxOdds;

    private String subPlayId;

    /**
     * 位置水差
     */
    private BigDecimal placeMarketDiff;

    private Integer status;

    private Integer thirdDataSourceStatus;

}
