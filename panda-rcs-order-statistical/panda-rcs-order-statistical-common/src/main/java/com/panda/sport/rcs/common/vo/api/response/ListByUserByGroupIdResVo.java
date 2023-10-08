package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * <p>
 * 玩家组管理-用户列表 返回vo
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@ApiModel(value = "玩家组管理-用户列表  返回vo", description = "玩家组管理-用户列表  返回vo")
public class ListByUserByGroupIdResVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private String uid;

    @ApiModelProperty(value = "用户名称")
    private String username;

    @ApiModelProperty(value = "一级标签名称")
    private String tagName;

    @ApiModelProperty(value = "一级标签id")
    private Integer tagId;

    @ApiModelProperty(value = "商户编码")
    private String merchantCode;

    @ApiModelProperty(value = "用户输赢")
    private BigDecimal profit;

    @ApiModelProperty(value = "盈利率")
    private String rate;

    @ApiModelProperty(value = "近7日输赢金额")
    private BigDecimal sevenDayAmount;

    @ApiModelProperty(value = "二级标签sport")
    private String sportJson;

    @ApiModelProperty(value = "二级标签tournament")
    private String tournamentJson;

    @ApiModelProperty(value = "二级标签orderType")
    private String orderTypeJson;

    @ApiModelProperty(value = "二级标签play")
    private String playJson;

    @ApiModelProperty(value = "二级标签orderStage")
    private String orderStageJson;

    @ApiModelProperty(value = "假名")
    private String fakeName;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public BigDecimal getSevenDayAmount() {
        return sevenDayAmount;
    }

    public void setSevenDayAmount(BigDecimal sevenDayAmount) {
        this.sevenDayAmount = sevenDayAmount;
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
}
