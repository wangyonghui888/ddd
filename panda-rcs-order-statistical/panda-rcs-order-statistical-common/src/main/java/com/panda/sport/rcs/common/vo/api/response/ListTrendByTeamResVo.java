package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "投注偏好详情/财务特征详情-趋势-球队返回vo", description = "")
public class ListTrendByTeamResVo {

    @ApiModelProperty(value = "开始日期")
    private Long beginDate;

    @ApiModelProperty(value = "结束日期")
    private Long endDate;

    @ApiModelProperty(value = "数据")
    List<ListByTeamResVo> list;

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

    public List<ListByTeamResVo> getList() {
        return list;
    }

    public void setList(List<ListByTeamResVo> list) {
        this.list = list;
    }
}
