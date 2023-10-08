package com.panda.sport.rcs.customdb.entity;

/**
 * @author :  toney
 * @Project Name :  panda-rcs-order-statistical
 * @Package Name :  com.panda.sport.rcs.customdb.entity
 * @Description :  用户日期
 * @Date: 2021-01-13 11:02
 * @ModificationHistory Who    When    What
 * --------  ---------  --------------------------
 */
public class StaticsUserDateEntity {
    private Long uid;
    private String date;


    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
