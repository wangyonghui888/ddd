package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author derre
 * @date 2022-04-10
 */
@ApiModel(value = "通过指纹id获取关联用户列表–导出 外部使用 返回vo", description = "")
public class FingerprintUserLisRestVO {

    @ApiModelProperty(value = "用户投注特征标")
    private String userLabel;
    @ApiModelProperty(value = "关联天数")
    private String betDays;
    @ApiModelProperty(value = "投注金额")
    private String betAmount;
    @ApiModelProperty(value = "盈利金额")
    private String netAmount;
    @ApiModelProperty(value = "盈利率")
    private String netAmountRate;
    @ApiModelProperty(value = "胜率")
    private String winAmountRate;
    @ApiModelProperty(value = "商户名")
    private String merchantCode;
    @ApiModelProperty(value = "用户id")
    private String userId;
    @ApiModelProperty(value = "用户名")
    private String username;

    public String getUserLabel() {
        return userLabel;
    }

    public void setUserLabel(String userLabel) {
        this.userLabel = userLabel;
    }

    public String getBetDays() {
        return betDays;
    }

    public void setBetDays(String betDays) {
        this.betDays = betDays;
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

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
