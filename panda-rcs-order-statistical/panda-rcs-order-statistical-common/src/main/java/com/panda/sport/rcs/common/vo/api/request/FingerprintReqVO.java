package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @author derre
 * @date 2022-04-10
 */
@ApiModel(value = "指纹池管理列表 接收 对象", description = "")
public class FingerprintReqVO {

    @ApiModelProperty(value = "成功投注金额")
    private String betAmount;
    @ApiModelProperty(value = "排序列 和返回字段保持一致")
    private String columnKey;
    @ApiModelProperty(value = "查询开始日期")
    @NotBlank(message = "开始日期不能为空")
    private String startTime;
    @ApiModelProperty(value = "查询结束日期")
    @NotBlank(message = "结束日期不能为空")
    private String endTime;
    @ApiModelProperty(value = "指纹id")
    private String fingerprintId;
    @ApiModelProperty(value = "平台盈利")
    private String netAmount;
    @ApiModelProperty(value = "排序方式:[降序:desc, 升序:asc]")
    private String order;
    @ApiModelProperty(value = "排序字段:[1:危险等级 2:关联用户 3：成功投注金额 4：平台盈利 5：平台盈利率 6：平台胜率 7:最后下注时间]")
    private Integer orderBy;
    @ApiModelProperty(value = "当前第几页")
    private Integer page;
    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;
    @ApiModelProperty(value = "rows")
    private Integer rows;
    @ApiModelProperty(value = "危险等级")
    private Integer riskLevel;
    @ApiModelProperty(value = "关联用户id")
    private String userIds;


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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFingerprintId() {
        return fingerprintId;
    }

    public void setFingerprintId(String fingerprintId) {
        this.fingerprintId = fingerprintId;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
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

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }
}
