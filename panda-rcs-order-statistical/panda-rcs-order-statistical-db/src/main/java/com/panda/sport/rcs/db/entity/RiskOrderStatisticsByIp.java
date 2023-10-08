package com.panda.sport.rcs.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel(value = "根据IP分组统计用户订单对象", description = "根据IP分组统计用户订单对象")
public class RiskOrderStatisticsByIp implements Serializable {

    private static final long serialVersionUID = 1L;

    public RiskOrderStatisticsByIp() {

    }

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "IP所属标签")
    private Integer tagId;

    @ApiModelProperty(value = "IP所属地区")
    private String area;

    @ApiModelProperty(value = "总投注额")
    private BigDecimal betAmount;

    @ApiModelProperty(value = "总盈亏额")
    private BigDecimal profitAmount;

    @ApiModelProperty(value = "盈利率")
    private BigDecimal profitProbability;

    @ApiModelProperty(value = "7天内总投注额")
    private BigDecimal sevenDaysBetAmount;

    @ApiModelProperty(value = "7天内总亏盈额")
    private BigDecimal sevenDaysProfitAmount;

    @ApiModelProperty(value = "最后下注时间")
    private Long finalBetTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public BigDecimal getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    public BigDecimal getProfitAmount() {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount) {
        this.profitAmount = profitAmount;
    }

    public BigDecimal getProfitProbability() {
        return profitProbability;
    }

    public void setProfitProbability(BigDecimal profitProbability) {
        this.profitProbability = profitProbability;
    }

    public BigDecimal getSevenDaysBetAmount() {
        return sevenDaysBetAmount;
    }

    public void setSevenDaysBetAmount(BigDecimal sevenDaysBetAmount) {
        this.sevenDaysBetAmount = sevenDaysBetAmount;
    }

    public BigDecimal getSevenDaysProfitAmount() {
        return sevenDaysProfitAmount;
    }

    public void setSevenDaysProfitAmount(BigDecimal sevenDaysProfitAmount) {
        this.sevenDaysProfitAmount = sevenDaysProfitAmount;
    }

    public Long getFinalBetTime() {
        return finalBetTime;
    }

    public void setFinalBetTime(Long finalBetTime) {
        this.finalBetTime = finalBetTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}


