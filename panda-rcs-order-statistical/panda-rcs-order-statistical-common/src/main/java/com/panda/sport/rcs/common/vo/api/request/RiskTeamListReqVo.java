package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "危险球队列表 接收 对象", description = "")
public class RiskTeamListReqVo {


    @ApiModelProperty(value = "当前第几页")
    private Integer page;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;

    @ApiModelProperty(value = "赛种ID")
    private List<Integer> regionIds;

    @ApiModelProperty(value = "赛种ID")
    private List<Integer> sportIds;

    @ApiModelProperty(value = "rows")
    private Integer rows;

    @ApiModelProperty(value = "模糊搜索： 1搜索id，2 搜索名称")
    private String searchType;

    @ApiModelProperty(value = "模糊搜索球队id或球队名称")
    private String teamLike;

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

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getTeamLike() {
        return teamLike;
    }

    public void setTeamLike(String teamLike) {
        this.teamLike = teamLike;
    }

}
