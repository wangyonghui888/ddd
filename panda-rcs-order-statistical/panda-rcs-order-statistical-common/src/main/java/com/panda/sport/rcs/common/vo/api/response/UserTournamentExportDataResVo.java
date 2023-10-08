package com.panda.sport.rcs.common.vo.api.response;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = " 体育联赛投注统计导出数据 返回vo", description = "")
public class UserTournamentExportDataResVo {

    @ApiModelProperty(value = "总投注金额")
    private Double betAmount;

    @ApiModelProperty(value = "投注日期")
    private String dateId;

    @ApiModelProperty(value = "成功投注场次")
    private Double matchCount;

    @ApiModelProperty(value = "平台盈利")
    private Double netAmount;

    @ApiModelProperty(value = "平台盈利率")
    private Double netAmountAvg;

    @ApiModelProperty(value = "笔均投注")
    private Double orderAvg;

    @ApiModelProperty(value = "总投注单数")
    private Double orderCount;

    @ApiModelProperty(value = "赛种id")
    private String sportId;

    @ApiModelProperty(value = "赛种名称英文")
    private String sportNameEn;

    @ApiModelProperty(value = "赛种名称中文简体")
    private String sportNameZs;

    @ApiModelProperty(value = "联赛id")
    private String tournamentId;

    @ApiModelProperty(value = "联赛名称英文")
    private String tournamentNameEn;

    @ApiModelProperty(value = "联赛名称中文简体")
    private String tournamentNameZs;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "用户盈利单数")
    private Double userWinOrderCount;

    @ApiModelProperty(value = "用户胜率")
    private Double userWinRatio;

    @ApiModelProperty(value = "有效投注金额")
    private Double validBetAmount;

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

    public Double getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Double matchCount) {
        this.matchCount = matchCount;
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

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getTournamentNameEn() {
        return tournamentNameEn;
    }

    public void setTournamentNameEn(String tournamentNameEn) {
        this.tournamentNameEn = tournamentNameEn;
    }

    public String getTournamentNameZs() {
        return tournamentNameZs;
    }

    public void setTournamentNameZs(String tournamentNameZs) {
        this.tournamentNameZs = tournamentNameZs;
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

    public Double getUserWinOrderCount() {
        return userWinOrderCount;
    }

    public void setUserWinOrderCount(Double userWinOrderCount) {
        this.userWinOrderCount = userWinOrderCount;
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
}
