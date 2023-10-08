package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "投注偏好详情/财务特征详情-趋势-联赛返回vo", description = "")
public class ListTrendByTournamentResVo {

    @ApiModelProperty(value = "开始日期")
    private Long beginDate;
    @ApiModelProperty(value = "结束日期")
    private Long endDate;
    List<ListByTournamentResVo> list;

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

    public List<ListByTournamentResVo> getList() {
        return list;
    }

    public void setList(List<ListByTournamentResVo> list) {
        this.list = list;
    }
}
