package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author derre
 * @date 2022-03-29
 */
@ApiModel(value = "危险单关监控导出 返回 对象", description = "")
public class RiskOrderExportVORiskOrderExportVO {

    @ApiModelProperty(value = "地区")
    private String area;

    @ApiModelProperty(value = "开赛时间")
    private String beginTime;

    @ApiModelProperty(value = "投注金额")
    private String betAmount;

    @ApiModelProperty(value = "创建时间(投注时间)")
    private String createTime;

    @ApiModelProperty(value = "设备ID")
    private String deviceId;

    @ApiModelProperty(value = "1:H5，2：PC,3:Android,4:IOS")
    private String deviceType;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "盘口类型")
    private String marketType;

    @ApiModelProperty(value = "赛事id")
    private String matchId;

    @ApiModelProperty(value = "赛事信息")
    private String matchInfo;

    @ApiModelProperty(value = "赛事类型：1 ：早盘赛事 ，2： 滚球盘赛事，3： 冠军盘赛事，5：活动赛事")
    private String matchType;

    @ApiModelProperty(value = "商户编码")
    private String merchantCode;

    @ApiModelProperty(value = "注单赔率")
    private String oddsValue;

    @ApiModelProperty(value = "订单单号")
    private String orderNo;

    @ApiModelProperty(value = "玩法名称")
    private String playName;

    @ApiModelProperty(value = "投注项名称")
    private String playOption;

    @ApiModelProperty(value = "危险说明")
    private String riskDesc;

    @ApiModelProperty(value = "危险类型")
    private String riskType;

    @ApiModelProperty(value = "序号")
    private Integer seq;

    @ApiModelProperty(value = "赛种名称")
    private String sportName;

    @ApiModelProperty(value = "联赛名称")
    private String tournamentName;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户投注标签")
    private String userTagName;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchInfo() {
        return matchInfo;
    }

    public void setMatchInfo(String matchInfo) {
        this.matchInfo = matchInfo;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getOddsValue() {
        return oddsValue;
    }

    public void setOddsValue(String oddsValue) {
        this.oddsValue = oddsValue;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public String getPlayOption() {
        return playOption;
    }

    public void setPlayOption(String playOption) {
        this.playOption = playOption;
    }

    public String getRiskDesc() {
        return riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
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

    public String getUserTagName() {
        return userTagName;
    }

    public void setUserTagName(String userTagName) {
        this.userTagName = userTagName;
    }
}
