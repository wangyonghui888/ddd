package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "体育联赛投注统计 接收 对象", description = "")
public class UserTournamentReqVo {

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "排序规则 asc desc")
    private String order;

    @ApiModelProperty(value = "排序字段 1 成功投注金额 2 成功笔数 3 笔均投注 4平台盈利 5 平台盈利率 6 用户胜率")
    private String orderBy;

    @ApiModelProperty(value = "赛种ids")
    private List<String> sportIds;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public List<String> getSportIds() {
        return sportIds;
    }

    public void setSportIds(List<String> sportIds) {
        this.sportIds = sportIds;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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
}
