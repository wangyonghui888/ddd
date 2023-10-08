package com.panda.sport.rcs.customdb.entity;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.entity
 * @description :
 * @date: 2020-07-19 14:08
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */

public class MarketOptionEntity {

    /*** 数据id ***/
    private Long id;

    /*** 赔率,单位:单位: 0.0001 ***/
    private int oddsValue;

    /*** 盘口id ***/
    private Long marketId;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOddsValue() {
        return oddsValue;
    }

    public void setOddsValue(int oddsValue) {
        this.oddsValue = oddsValue;
    }


    public Long getMarketId() {
        return marketId;
    }

    public void setMarketId(Long marketId) {
        this.marketId = marketId;
    }
}

