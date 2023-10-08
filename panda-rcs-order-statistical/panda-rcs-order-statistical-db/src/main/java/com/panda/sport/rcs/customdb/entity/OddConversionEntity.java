package com.panda.sport.rcs.customdb.entity;

/**
 * @author :  dorich
 * @project Name :  panda-rcs-order-statistical
 * @package Name :  com.panda.sport.rcs.customdb.entity
 * @description :  赔率转换关系表
 * @date: 2020-07-21 10:06
 * @modificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class OddConversionEntity {

    /*** 欧赔 ***/
    private String euOdds;

    /*** 马赔 ***/
    private String myOdds;

    public String getEuOdds() {
        return euOdds;
    }

    public void setEuOdds(String euOdds) {
        this.euOdds = euOdds;
    }

    public String getMyOdds() {
        return myOdds;
    }

    public void setMyOdds(String myOdds) {
        this.myOdds = myOdds;
    }
}
