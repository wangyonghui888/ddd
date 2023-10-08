package com.panda.sport.rcs.common.vo.api.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panda.sport.rcs.common.jsonserialize.Decimal2Serializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "投注偏好详情/财务特征详情-联赛返回vo", description = "")
public class ListByTournamentResVo {

    @ApiModelProperty(value = "联赛id")
    private Long tournamentId;

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

    @ApiModelProperty(value = "联赛名称")
    private String tournamentName;

    @ApiModelProperty(value = "运动种类名称")
    private String sportName;


    /**
     * 赢率
     */
    private Double winPoint;

    public String getSportName() {
        return sportName;
    }

    public BigDecimal getFinanceValue() {
        return financeValue;
    }

    public void setFinanceValue(BigDecimal financeValue) {
        this.financeValue = financeValue;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
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

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }


    public Double getWinPoint() {
        if(winPoint == null){
            return 0D;
        }
        return winPoint;
    }

    public void setWinPoint(Double winPoint) {
        this.winPoint = winPoint;
    }
}
