package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = " 根据IP查询所有关联用户 返回vo", description = "")
public class UserListByIpResVo {

    @ApiModelProperty(value = "用户ID")
    private String userId;
    @ApiModelProperty(value = "用户名称")
    private String userName;
    @ApiModelProperty(value = "商户编码")
    private String merchantCode;
    @ApiModelProperty(value = "一级标签")
    private String tagName;

    @ApiModelProperty(value = "sport二级标签")
    private String sportJson;
    @ApiModelProperty(value = " tournament二级标签")
    private String tournamentJson;
    @ApiModelProperty(value = "orderType二级标签")
    private String orderTypeJson;
    @ApiModelProperty(value = "play二级标签")
    private String playJson;
    @ApiModelProperty(value = "orderStage二级标签")
    private String orderStageJson;

    @ApiModelProperty(value = "sport二级标签")
    private String sportIdsJson;
    @ApiModelProperty(value = " tournament二级标签")
    private String tournamentIdsJson;
    @ApiModelProperty(value = "orderType二级标签")
    private String orderTypeIdsJson;
    @ApiModelProperty(value = "play二级标签")
    private String playIdsJson;
    @ApiModelProperty(value = "orderStage二级标签")
    private String orderStageIdsJson;

    @ApiModelProperty(value = "7天内总投注")
    private BigDecimal sevenDayBetAmount;
    @ApiModelProperty(value = "7天内总输赢")
    private BigDecimal sevenDayProfitAmount;
    @ApiModelProperty(value = "关联天数")
    private int days;
    @ApiModelProperty(value = "标签ID")
    private Integer tagId;
    @ApiModelProperty(value = "假名")
    private String fakeName;

    public String getSportIdsJson() {
        return sportIdsJson;
    }

    public void setSportIdsJson(String sportIdsJson) {
        this.sportIdsJson = sportIdsJson;
    }

    public String getTournamentIdsJson() {
        return tournamentIdsJson;
    }

    public void setTournamentIdsJson(String tournamentIdsJson) {
        this.tournamentIdsJson = tournamentIdsJson;
    }

    public String getOrderTypeIdsJson() {
        return orderTypeIdsJson;
    }

    public void setOrderTypeIdsJson(String orderTypeIdsJson) {
        this.orderTypeIdsJson = orderTypeIdsJson;
    }

    public String getPlayIdsJson() {
        return playIdsJson;
    }

    public void setPlayIdsJson(String playIdsJson) {
        this.playIdsJson = playIdsJson;
    }

    public String getOrderStageIdsJson() {
        return orderStageIdsJson;
    }

    public void setOrderStageIdsJson(String orderStageIdsJson) {
        this.orderStageIdsJson = orderStageIdsJson;
    }

    public String getFakeName() {
        return fakeName;
    }

    public void setFakeName(String fakeName) {
        this.fakeName = fakeName;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
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

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getSportJson() {
        return sportJson;
    }

    public void setSportJson(String sportJson) {
        this.sportJson = sportJson;
    }

    public String getTournamentJson() {
        return tournamentJson;
    }

    public void setTournamentJson(String tournamentJson) {
        this.tournamentJson = tournamentJson;
    }

    public String getOrderTypeJson() {
        return orderTypeJson;
    }

    public void setOrderTypeJson(String orderTypeJson) {
        this.orderTypeJson = orderTypeJson;
    }

    public String getPlayJson() {
        return playJson;
    }

    public void setPlayJson(String playJson) {
        this.playJson = playJson;
    }

    public String getOrderStageJson() {
        return orderStageJson;
    }

    public void setOrderStageJson(String orderStageJson) {
        this.orderStageJson = orderStageJson;
    }

    public BigDecimal getSevenDayBetAmount() {
        return sevenDayBetAmount;
    }

    public void setSevenDayBetAmount(BigDecimal sevenDayBetAmount) {
        this.sevenDayBetAmount = sevenDayBetAmount;
    }

    public BigDecimal getSevenDayProfitAmount() {
        return sevenDayProfitAmount;
    }

    public void setSevenDayProfitAmount(BigDecimal sevenDayProfitAmount) {
        this.sevenDayProfitAmount = sevenDayProfitAmount;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
