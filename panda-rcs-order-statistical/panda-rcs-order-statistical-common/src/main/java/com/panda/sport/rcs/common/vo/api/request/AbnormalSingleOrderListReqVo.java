package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "危险单关监控 接收 对象", description = "")
public class AbnormalSingleOrderListReqVo {

    @ApiModelProperty(value = "投注金额")
    private String betAmount;

    @ApiModelProperty(value = "统计类型：[1:自然日，2账务日]")
    private Integer dataType;

    @ApiModelProperty(value = "设备类型：1:H5，2：PC,3:Android,4:IOS")
    private String deviceType;

    @ApiModelProperty(value = "开始日期 yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @ApiModelProperty(value = "结束日期 yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "赛事长ID")
    private String matchManagerId;

    @ApiModelProperty(value = "商户")
    private List<String> merchantIds;

    @ApiModelProperty(value = "当前第几页")
    private Integer page;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;

    @ApiModelProperty(value = "危险类型 [1：ARB 2：Goal Bet 3：Group Bet]")
    private String riskType;

    @ApiModelProperty(value = "rows")
    private Integer rows;

    @ApiModelProperty(value = "赛种")
    private List<Integer> sportIds;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "注单号")
    private String orderNo;

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMatchManagerId() {
        return matchManagerId;
    }

    public void setMatchManagerId(String matchManagerId) {
        this.matchManagerId = matchManagerId;
    }

    public List<String> getMerchantIds() {
        return merchantIds;
    }

    public void setMerchantIds(List<String> merchantIds) {
        this.merchantIds = merchantIds;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public List<Integer> getSportIds() {
        return sportIds;
    }

    public void setSportIds(List<Integer> sportIds) {
        this.sportIds = sportIds;
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
