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

public class MarketEntity {

    /*** 数据id ***/
    private Long id;

    /*** 盘口值 ***/
    private String marketValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(String marketValue) {
        this.marketValue = marketValue;
    }
}

