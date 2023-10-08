package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "投注偏好详情/财务特征详情-趋势-玩法返回vo", description = "")
public class ListTrendByPlayResVo {

    @ApiModelProperty(value = "开始日期")
    private Long beginDate;

    @ApiModelProperty(value = "结束日期")
    private Long endDate;

    @ApiModelProperty(value = "数据")
    List<ListByPlayResVo> list;

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

    public List<ListByPlayResVo> getList() {
        return list;
    }

    public void setList(List<ListByPlayResVo> list) {
        this.list = list;
    }
}
