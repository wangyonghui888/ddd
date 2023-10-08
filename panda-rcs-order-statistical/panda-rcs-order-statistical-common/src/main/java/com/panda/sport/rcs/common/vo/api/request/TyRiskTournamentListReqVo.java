package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "危险联赛列表 接收 对象", description = "")
public class TyRiskTournamentListReqVo {


    @ApiModelProperty(value = "当前第几页")
    private Integer page;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;

    @ApiModelProperty(value = "模糊匹配类型 1-联赛ID;2-联赛名称")
    private Integer fuzzyQueryType;

    @ApiModelProperty(value = "联赛区域")
    private List<Integer> regionIds;

    @ApiModelProperty(value = "赛种id")
    private List<Integer> sportIds;

    @ApiModelProperty(value = "联赛ID/名称模糊匹配字段")
    private String tournamentLike;

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

    public Integer getFuzzyQueryType() {
        return fuzzyQueryType;
    }

    public void setFuzzyQueryType(Integer fuzzyQueryType) {
        this.fuzzyQueryType = fuzzyQueryType;
    }

    public List<Integer> getRegionIds() {
        return regionIds;
    }

    public void setRegionIds(List<Integer> regionIds) {
        this.regionIds = regionIds;
    }

    public List<Integer> getSportIds() {
        return sportIds;
    }

    public void setSportIds(List<Integer> sportIds) {
        this.sportIds = sportIds;
    }

    public String getTournamentLike() {
        return tournamentLike;
    }

    public void setTournamentLike(String tournamentLike) {
        this.tournamentLike = tournamentLike;
    }
}
