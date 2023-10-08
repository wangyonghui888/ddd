package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "获取危险ip池列表导出 接收 对象", description = "")
public class RiskIpPoolUserIpRiskOutPutListReqVo {

    @ApiModelProperty(value = "排序字段:[1:地区,2:ip标签,3:关联用户,4:成功投注金额,5:平台盈利,6:平台盈利率,7:平台胜率,8:最后下注时间]")
    private Integer orderBy;

    @ApiModelProperty(value = "排序方式:[降序:desc, 升序:asc]")
    private String order;

    @ApiModelProperty(value = "开始时间")
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    @ApiModelProperty(value = "成功投注金额")
    private String betAmount;

    @ApiModelProperty(value = "平台输赢")
    private String netAmount;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "汇率")
    private Boolean isAddRate = false;

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
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


    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Boolean getIsAddRate() {
        return isAddRate;
    }

    public void setIsAddRate(Boolean addRate) {
        this.isAddRate = addRate;
    }
}
