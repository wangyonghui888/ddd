package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "投注偏好详情/财务特征详情-球队返回vo", description = "")
public class ListByTeamResVo {

    @ApiModelProperty(value = "球队id")
    private Long teamId;

    @ApiModelProperty(value = "球队名称")
    private String teamName;

    @ApiModelProperty(value = "次数")
    private BigDecimal betAmount;


    @ApiModelProperty(value = "运动种类名称")
    private String sportName;

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }


}
