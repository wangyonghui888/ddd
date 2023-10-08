package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author derre
 */
@ApiModel(value = "危险指纹池管理列表–导出 返回vo", description = "")
public class RiskFpListResp {

    @ApiModelProperty(value = "指纹id")
    private String fingerprintId;
    @ApiModelProperty(value = "设备类型")
    private String device;
    @ApiModelProperty(value = "最后下注时间")
    private String maxBetTime;
    @ApiModelProperty(value = "投注金额")
    private String betAmount;
    @ApiModelProperty(value = "盈利金额")
    private String netAmount;
    @ApiModelProperty(value = "盈利率")
    private String netAmountRate;
    @ApiModelProperty(value = "胜率")
    private String winAmountRate;
    @ApiModelProperty(value = "危险等级")
    private String riskLevel;
    @ApiModelProperty(value = "关联用户数量")
    private Integer userCount;
    @ApiModelProperty(value = "备注")
    private String remark;

    public String getFingerprintId() {
        return fingerprintId;
    }

    public void setFingerprintId(String fingerprintId) {
        this.fingerprintId = fingerprintId;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getMaxBetTime() {
        return maxBetTime;
    }

    public void setMaxBetTime(String maxBetTime) {
        this.maxBetTime = maxBetTime;
    }

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
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

    public String getWinAmountRate() {
        return winAmountRate;
    }

    public void setWinAmountRate(String winAmountRate) {
        this.winAmountRate = winAmountRate;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
