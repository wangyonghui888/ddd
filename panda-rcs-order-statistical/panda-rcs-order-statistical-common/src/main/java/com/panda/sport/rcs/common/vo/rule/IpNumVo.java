package com.panda.sport.rcs.common.vo.rule;

/**
 * 访问特征类	R28	一机多登判断标准  IP判断
 *
 * @author lithan
 * @date 2020-07-01
 */
public class IpNumVo {
    //ip
    public String ip;
    //账户数
    public Integer num;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}

