package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "危险用户监控列表 接收 对象", description = "")
public class AbnormalMatchUserListReqVo {

    @ApiModelProperty(value = "异常类型1：危险串关投注")
    private Integer abnormalType;

    @ApiModelProperty(value = "危险串关投注金额（单位：元）")
    private String betAmount;

    @ApiModelProperty(value = "排序列 和返回字段保持一致")
    private String columnKey;

    @ApiModelProperty(value = "1:自然日，2账务日")
    private Integer dataType;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "日期id yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "赛事ID")
    private String matchId;

    @ApiModelProperty(value = "monitorOrderCount")
    private Integer monitorOrderCount;

    @ApiModelProperty(value = "危险串关投注笔数")
    private Integer orderCount;

    @ApiModelProperty(value = "排序方式 desc-降序 asc-升序")
    private String order;

    @ApiModelProperty(value = "当前第几页")
    private Integer page;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;

    @ApiModelProperty(value = "rows")
    private Integer rows;

    @ApiModelProperty(value = "timeList")
    private List<String> timeList;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    public Integer getAbnormalType() {
        return abnormalType;
    }

    public void setAbnormalType(Integer abnormalType) {
        this.abnormalType = abnormalType;
    }

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

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
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

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<String> getTimeList() {
        return timeList;
    }

    public void setTimeList(List<String> timeList) {
        this.timeList = timeList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }
}
