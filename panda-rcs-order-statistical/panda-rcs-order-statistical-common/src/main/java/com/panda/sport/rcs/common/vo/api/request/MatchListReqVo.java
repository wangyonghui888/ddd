package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "危险赛事列表 接收 对象", description = "")
public class MatchListReqVo {

    @ApiModelProperty(value = "危险串关投注金额（单位：元）")
    private String betAmount;

    @ApiModelProperty(value = "统计类型,日：[1:自然日，2账务日]")
    private Integer dateType;

    @ApiModelProperty(value = "賽事管理ID ( 長ID )")
    private String matchManageId="";

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "赛事ID")
    private String matchId;

    @ApiModelProperty(value = "危险串关投注笔数")
    private String orderCount="1";

    @ApiModelProperty(value = "排序方式 desc-降序 asc-升序")
    private String order;

    @ApiModelProperty(value = "排序字段:[1: 投注笔数， 2：投注金额，3：投注人数 ,4:期望赔付 ， 5：实际盈亏 ]")
    private String orderBy="1";

    @ApiModelProperty(value = "当前第几页")
    private Integer page=1;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize=30;

    @ApiModelProperty(value = "seriesTypes")
    private List<Integer> seriesTypes;

    @ApiModelProperty(value = "危投注人数")
    private List<Integer> sportIds;

    @ApiModelProperty(value = "rows")
    private Integer rows;

    @ApiModelProperty(value = "联赛id")
    private List<Integer> tournamentIds;

    @ApiModelProperty(value = "联赛等级")
    private List<Integer> tournamentLevels;

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public Integer getDateType() {
        return dateType;
    }

    public void setDateType(Integer dateType) {
        this.dateType = dateType;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        this.orderCount = orderCount;
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

    public List<Integer> getSeriesTypes() {
        return seriesTypes;
    }

    public void setSeriesTypes(List<Integer> seriesTypes) {
        this.seriesTypes = seriesTypes;
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

    public List<Integer> getTournamentIds() {
        return tournamentIds;
    }

    public void setTournamentIds(List<Integer> tournamentIds) {
        this.tournamentIds = tournamentIds;
    }

    public List<Integer> getTournamentLevels() {
        return tournamentLevels;
    }

    public void setTournamentLevels(List<Integer> tournamentLevels) {
        this.tournamentLevels = tournamentLevels;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getMatchManageId() {
        return matchManageId;
    }

    public void setMatchManageId(String matchManageId) {
        this.matchManageId = matchManageId;
    }
}
