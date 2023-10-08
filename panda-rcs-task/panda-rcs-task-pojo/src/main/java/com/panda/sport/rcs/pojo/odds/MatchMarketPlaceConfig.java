package com.panda.sport.rcs.pojo.odds;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MatchMarketPlaceConfig {

    private Integer placeNum;

    private String spread;

    private String oldMargin;

    private String minOdds;

    private String maxOdds;

    /**
     * 位置水差
     */
    private BigDecimal placeMarketDiff;

    private Integer status;

    private Integer thirdDataSourceStatus;

}
