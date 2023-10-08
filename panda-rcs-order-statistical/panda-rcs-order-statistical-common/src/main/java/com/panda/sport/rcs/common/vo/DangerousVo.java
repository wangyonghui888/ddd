package com.panda.sport.rcs.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "危险投注行为 内部 vo", description = "")
public class DangerousVo {

    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "开始日期")
    private long beginTime;
    @ApiModelProperty(value = "结束日期")
    private long endTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
