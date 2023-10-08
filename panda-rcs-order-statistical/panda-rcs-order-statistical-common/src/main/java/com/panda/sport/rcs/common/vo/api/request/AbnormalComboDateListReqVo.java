package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "危险串关监控 接收 对象", description = "")
public class AbnormalComboDateListReqVo {

    @ApiModelProperty(value = "危险串关投注金额（单位：元）")
    private String betAmount;

    @ApiModelProperty(value = "排序列 和返回字段保持一致")
    private String columnKey;

    @ApiModelProperty(value = "投注详情HashCode")
    private String contentCode;

    @ApiModelProperty(value = "查询串关用户contentCode 集合")
    private List<String> contentCodeList;

    @ApiModelProperty(value = "1:投注时间,2结算时间，3开赛时间")
    private Integer dataType;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "日期id yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "期望盈利最大值")
    private Integer expectPayoutMax;

    @ApiModelProperty(value = "期望盈利最小值")
    private Integer expectPayoutMin;

    @ApiModelProperty(value = "monitorOrderCount")
    private Integer monitorOrderCount;

    @ApiModelProperty(value = "排序方式 desc-降序 asc-升序")
    private String order;

    @ApiModelProperty(value = "排序字段:[1: 投注笔数， 2：投注金额，3：投注人数 ,4:期望赔付 ， 5：实际盈亏 ]")
    private String orderBy;

    @ApiModelProperty(value = "危险串关投注笔数")
    private String orderCountEnd;

    @ApiModelProperty(value = "危险串关投注笔数")
    private String orderCountStart;

    @ApiModelProperty(value = "当前第几页")
    private Integer page;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;

    @ApiModelProperty(value = "危投注人数")
    private String playerCountEnd;

    @ApiModelProperty(value = "危投注人数")
    private String playerCountStart;

    @ApiModelProperty(value = "串关类型")
    private Integer seriesType;

    @ApiModelProperty(value = "rows")
    private Integer rows;

    @ApiModelProperty(value = "timeList")
    private List<String> timeList;

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public String getColumnKey() {
        return columnKey;
    }

    public void setColumnKey(String columnKey) {
        this.columnKey = columnKey;
    }

    public String getContentCode() {
        return contentCode;
    }

    public void setContentCode(String contentCode) {
        this.contentCode = contentCode;
    }

    public List<String> getContentCodeList() {
        return contentCodeList;
    }

    public void setContentCodeList(List<String> contentCodeList) {
        this.contentCodeList = contentCodeList;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getExpectPayoutMax() {
        return expectPayoutMax;
    }

    public void setExpectPayoutMax(Integer expectPayoutMax) {
        this.expectPayoutMax = expectPayoutMax;
    }

    public Integer getExpectPayoutMin() {
        return expectPayoutMin;
    }

    public void setExpectPayoutMin(Integer expectPayoutMin) {
        this.expectPayoutMin = expectPayoutMin;
    }

    public Integer getMonitorOrderCount() {
        return monitorOrderCount;
    }

    public void setMonitorOrderCount(Integer monitorOrderCount) {
        this.monitorOrderCount = monitorOrderCount;
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

    public String getOrderCountEnd() {
        return orderCountEnd;
    }

    public void setOrderCountEnd(String orderCountEnd) {
        this.orderCountEnd = orderCountEnd;
    }

    public String getOrderCountStart() {
        return orderCountStart;
    }

    public void setOrderCountStart(String orderCountStart) {
        this.orderCountStart = orderCountStart;
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

    public String getPlayerCountEnd() {
        return playerCountEnd;
    }

    public void setPlayerCountEnd(String playerCountEnd) {
        this.playerCountEnd = playerCountEnd;
    }

    public String getPlayerCountStart() {
        return playerCountStart;
    }

    public void setPlayerCountStart(String playerCountStart) {
        this.playerCountStart = playerCountStart;
    }

    public Integer getSeriesType() {
        return seriesType;
    }

    public void setSeriesType(Integer seriesType) {
        this.seriesType = seriesType;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public List<String> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<String> timeList) {
        this.timeList = timeList;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
