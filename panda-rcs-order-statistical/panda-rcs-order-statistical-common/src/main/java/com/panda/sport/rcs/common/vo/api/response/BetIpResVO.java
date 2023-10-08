package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author derre
 * @date 2022-03-29
 */
@ApiModel(value = "获取投注ip池列表导出 返回vo", description = "")
public class BetIpResVO {

    @ApiModelProperty(value = "地区")
    private String area;

    @ApiModelProperty(value = "成功投注金额")
    private String betAmount;

    @ApiModelProperty(value = "查询ip地址")
    private String ip;

    @ApiModelProperty(value = "ip标签")
    private String ipLabel;

    @ApiModelProperty(value = "最后下注时间")
    private String maxBetTime;

    @ApiModelProperty(value = "平台盈利")
    private String netAmount;

    @ApiModelProperty(value = "盈利率")
    private String netAmountRate;

    @ApiModelProperty(value = "关联用户数")
    private Integer userCount;

    @ApiModelProperty(value = "胜率")
    private String winAmountRate;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIpLabel() {
        return ipLabel;
    }

    public void setIpLabel(String ipLabel) {
        this.ipLabel = ipLabel;
    }

    public String getMaxBetTime() {
        return maxBetTime;
    }

    public void setMaxBetTime(String maxBetTime) {
        this.maxBetTime = maxBetTime;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getNetAmountRate() {
        return netAmountRate;
    }

    public void setNetAmountRate(String netAmountRate) {
        this.netAmountRate = netAmountRate;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public String getWinAmountRate() {
        return winAmountRate;
    }

    public void setWinAmountRate(String winAmountRate) {
        this.winAmountRate = winAmountRate;
    }
}
