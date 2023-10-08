package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = " 用户行为详情 危险投注行为 返回vo", description = "")
public class UserDangerousOrderResVo {

    @ApiModelProperty(value = "危险行为ID ")
    private int dangerousId;
    @ApiModelProperty(value = "危险行为名称 ")
    private String ruleName;
    @ApiModelProperty(value = "投注金额")
    private BigDecimal betAmount;
    @ApiModelProperty(value = "笔均投注金额")
    private BigDecimal avgAmount;
    @ApiModelProperty(value = "注单笔数")
    private int num;

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public int getDangerousId() {
        return dangerousId;
    }

    public void setDangerousId(int dangerousId) {
        this.dangerousId = dangerousId;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public BigDecimal getAvgAmount() {
        return avgAmount;
    }

    public void setAvgAmount(BigDecimal avgAmount) {
        this.avgAmount = avgAmount;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
