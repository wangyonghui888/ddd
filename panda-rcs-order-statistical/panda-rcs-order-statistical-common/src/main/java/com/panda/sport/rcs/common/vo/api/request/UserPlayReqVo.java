package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "体育玩家玩法统计 接收 对象", description = "")
public class UserPlayReqVo {

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "升序/降序")
    private String order;

    @ApiModelProperty(value = "排序的位置")
    private String orderBy;

    @ApiModelProperty(value = "玩法id")
    private String playId;

    @ApiModelProperty(value = "赛种id")
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

    public String getPlayId() {
        return playId;
    }

    public void setPlayId(String playId) {
        this.playId = playId;
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
