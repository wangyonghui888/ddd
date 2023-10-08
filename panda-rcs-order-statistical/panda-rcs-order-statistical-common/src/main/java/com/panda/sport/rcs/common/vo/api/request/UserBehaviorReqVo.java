package com.panda.sport.rcs.common.vo.api.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "投注偏好详情-用户行为详情-请求vo", description = "")
public class UserBehaviorReqVo {

    @ApiModelProperty(value = "用户ID")
    @NotNull
    private Long userId;

    @ApiModelProperty(value = "开始日期")
    @NotNull
    private Long beginDate;

    @ApiModelProperty(value = "结束日期")
    @NotNull
    private Long endDate;

    @ApiModelProperty(value = "排序字段(降序)")
    private String orderColumn;

    //内部参数  标识是否统计汇总
    @JsonIgnore
    private Integer isAll;

    public Long getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Long beginDate) {
        this.beginDate = beginDate;
    }

    public Long getEndDate() {
        return endDate;
    }

    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }

    public String getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(String orderColumn) {
        this.orderColumn = orderColumn;
    }

    public Integer getIsAll() {
        return isAll;
    }

    public void setIsAll(Integer isAll) {
        this.isAll = isAll;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
