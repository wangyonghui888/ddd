package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "投注偏好详情/财务特征详情-趋势-球类返回vo", description = "")
public class ListTrendBySportResVo {
    @ApiModelProperty(value = "开始日期")
    private Long beginDate;
    @ApiModelProperty(value = "结束日期")
    private Long endDate;
    List<ListBySportResVo> list;

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

    public List<ListBySportResVo> getList() {
        return list;
    }

    public void setList(List<ListBySportResVo> list) {
        this.list = list;
    }
}
