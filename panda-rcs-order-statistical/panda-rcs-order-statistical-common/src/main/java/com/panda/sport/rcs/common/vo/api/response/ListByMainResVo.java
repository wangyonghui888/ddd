package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "投注偏好详情/财务特征详情-对冲投注  返回vo", description = "")
public class ListByMainResVo {

    @ApiModelProperty(value = "正副盘 1正  2副")
    private int mainType;

    @ApiModelProperty(value = "正盘 副盘")
    private String name;

    @ApiModelProperty(value = "投注金额")
    private BigDecimal betAmount;

    public int getMainType() {
        return mainType;
    }

    public void setMainType(int mainType) {
        this.mainType = mainType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }
}
