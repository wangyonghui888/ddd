package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = " 根据IP查询投注统计信息 返回vo", description = "")
public class IpListResVo {

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "标签ID")
    private String tagId;

    @ApiModelProperty(value = "关联用户")
    private Integer userNum;

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

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getUserNum() {
        return userNum;
    }

    public void setUserNum(Integer userNum) {
        this.userNum = userNum;
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
