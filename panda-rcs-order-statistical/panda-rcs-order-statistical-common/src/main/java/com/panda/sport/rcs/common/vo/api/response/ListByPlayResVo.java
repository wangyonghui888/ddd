package com.panda.sport.rcs.common.vo.api.response;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.sport.rcs.common.jsonserialize.Decimal2Serializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "投注偏好详情/财务特征详情-玩法返回vo", description = "")
public class ListByPlayResVo {

    @ApiModelProperty(value = "球类id")
    private int sportId;

    @ApiModelProperty(value = "玩法id")
    private int playId;

    @ApiModelProperty(value = "投注金额(投注成功)")
    private BigDecimal betAmount;

    @ApiModelProperty(value = "投注金额(结算成功)")
    private BigDecimal financeValue;

    @ApiModelProperty(value = "投注笔数")
    private Integer betNum;

    @ApiModelProperty(value = "盈利投注笔数")
    private Integer betProfitNum;

    @ApiModelProperty(value = "笔均投注金额")
    @JsonSerialize(using = Decimal2Serializer.class)
    private BigDecimal avgBetAmount;

    @ApiModelProperty(value = "盈利金额")
    private BigDecimal profitAmount;

    @ApiModelProperty(value = "盈利率")
    @JsonSerialize(using = Decimal2Serializer.class)
    private BigDecimal profitRate;

    @ApiModelProperty(value = "盈利注单比")
    @JsonSerialize(using = Decimal2Serializer.class)
    private BigDecimal profitBetRate;

    @ApiModelProperty(value = "扩展字段 用于存放sportId和playId", hidden = true)
    private String sportIdPlayId;

    @ApiModelProperty(value = "玩法名称")
    private String playName;

    @ApiModelProperty(value = "运动种类名称")
    private String sportName;


    @ApiModelProperty(value = "胜率")
    private BigDecimal winPoint;

    public BigDecimal getFinanceValue() {
        return financeValue;
    }

    public void setFinanceValue(BigDecimal financeValue) {
        this.financeValue = financeValue;
    }

    public void setSportIdPlayId(String sportIdPlayId) {
        this.sportIdPlayId = sportIdPlayId;
    }

    public int getSportId() {
        if (ObjectUtils.isNotEmpty(sportIdPlayId)) {
            return Integer.parseInt(sportIdPlayId.split("-")[0]);
        }
        return sportId;
    }

    public void setSportId(int sportId) {
        this.sportId = sportId;
    }

    public int getPlayId() {
        if (ObjectUtils.isNotEmpty(sportIdPlayId)) {
            return Integer.parseInt(sportIdPlayId.split("-")[1]);
        }
        return playId;
    }

    public void setPlayId(int playId) {
        this.playId = playId;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public Integer getBetNum() {
        return betNum;
    }

    public void setBetNum(Integer betNum) {
        this.betNum = betNum;
    }

    public Integer getBetProfitNum() {
        return betProfitNum;
    }

    public void setBetProfitNum(Integer betProfitNum) {
        this.betProfitNum = betProfitNum;
    }

    public BigDecimal getAvgBetAmount() {
        return avgBetAmount;
    }

    public void setAvgBetAmount(BigDecimal avgBetAmount) {
        this.avgBetAmount = avgBetAmount;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public BigDecimal getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(BigDecimal profitRate) {
        this.profitRate = profitRate;
    }

    public BigDecimal getProfitBetRate() {
        return profitBetRate;
    }

    public void setProfitBetRate(BigDecimal profitBetRate) {
        this.profitBetRate = profitBetRate;
    }

    public String getSportIdPlayId() {
        return sportIdPlayId;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public BigDecimal getWinPoint() {
        if(winPoint == null){
            return BigDecimal.ZERO;
        }
        return winPoint;
    }

    public void setWinPoint(BigDecimal winPoint) {
        this.winPoint = winPoint;
    }
}
