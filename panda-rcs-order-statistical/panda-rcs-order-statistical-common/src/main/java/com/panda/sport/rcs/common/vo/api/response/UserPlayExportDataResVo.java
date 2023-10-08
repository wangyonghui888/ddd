package com.panda.sport.rcs.common.vo.api.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = " 体育玩家玩法导出数据 返回vo", description = "")
public class UserPlayExportDataResVo {


    @ApiModelProperty(value = "成功投注金额")
    private Double betAmount;

    @ApiModelProperty(value = "投注日期")
    private String dateId;

    @ApiModelProperty(value = "平台盈利")
    private Double netAmount;

    @ApiModelProperty(value = "平台盈利率")
    private Double netAmountAvg;

    @ApiModelProperty(value = "笔均投注")
    private Double orderAvg;

    @ApiModelProperty(value = "成功投注笔数")
    private Double orderCount;

    @ApiModelProperty(value = "玩法id")
    private String playId;

    @ApiModelProperty(value = "玩法英文")
    private String playNameEn;

    @ApiModelProperty(value = "玩法中文")
    private String playNameZs;

    @ApiModelProperty(value = "赛种ID")
    private String sportId;

    @ApiModelProperty(value = "赛种名称英文")
    private String sportNameEn;

    @ApiModelProperty(value = "赛种名称中文简体")
    private String sportNameZs;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户胜率")
    private Double userWinRatio;

    @ApiModelProperty(value = "有效投注额")
    private Double validBetAmount;

    @ApiModelProperty(value = "中奖注单数")
    private Double winOrderCount;

    public Double getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(Double betAmount) {
        this.betAmount = betAmount;
    }

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    public Double getNetAmountAvg() {
        return netAmountAvg;
    }

    public void setNetAmountAvg(Double netAmountAvg) {
        this.netAmountAvg = netAmountAvg;
    }

    public Double getOrderAvg() {
        return orderAvg;
    }

    public void setOrderAvg(Double orderAvg) {
        this.orderAvg = orderAvg;
    }

    public Double getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Double orderCount) {
        this.orderCount = orderCount;
    }

    public String getPlayId() {
        return playId;
    }

    public void setPlayId(String playId) {
        this.playId = playId;
    }

    public String getPlayNameEn() {
        return playNameEn;
    }

    public void setPlayNameEn(String playNameEn) {
        this.playNameEn = playNameEn;
    }

    public String getPlayNameZs() {
        return playNameZs;
    }

    public void setPlayNameZs(String playNameZs) {
        this.playNameZs = playNameZs;
    }

    public String getSportId() {
        return sportId;
    }

    public void setSportId(String sportId) {
        this.sportId = sportId;
    }

    public String getSportNameEn() {
        return sportNameEn;
    }

    public void setSportNameEn(String sportNameEn) {
        this.sportNameEn = sportNameEn;
    }

    public String getSportNameZs() {
        return sportNameZs;
    }

    public void setSportNameZs(String sportNameZs) {
        this.sportNameZs = sportNameZs;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Double getUserWinRatio() {
        return userWinRatio;
    }

    public void setUserWinRatio(Double userWinRatio) {
        this.userWinRatio = userWinRatio;
    }

    public Double getValidBetAmount() {
        return validBetAmount;
    }

    public void setValidBetAmount(Double validBetAmount) {
        this.validBetAmount = validBetAmount;
    }

    public Double getWinOrderCount() {
        return winOrderCount;
    }

    public void setWinOrderCount(Double winOrderCount) {
        this.winOrderCount = winOrderCount;
    }
}
