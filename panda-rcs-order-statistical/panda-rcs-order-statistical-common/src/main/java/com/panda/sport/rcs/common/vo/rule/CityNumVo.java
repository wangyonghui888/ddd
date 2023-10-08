package com.panda.sport.rcs.common.vo.rule;

import java.time.LocalDateTime;

/**
 * 投注内容-代理登录判断标准
 *
 * @author lithan
 * @date 2020-07-01
 */
public class CityNumVo {
    //球队ID
    public Long loginDate;
    //次数
    public Long num;
    //城市
    public Long cityNum;

    public Long getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Long loginDate) {
        this.loginDate = loginDate;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public Long getCityNum() {
        return cityNum;
    }

    public void setCityNum(Long cityNum) {
        this.cityNum = cityNum;
    }
}

