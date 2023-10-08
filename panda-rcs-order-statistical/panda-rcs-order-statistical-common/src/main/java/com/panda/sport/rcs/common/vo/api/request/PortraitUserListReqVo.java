package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "体育用户列表 接收 对象", description = "")
public class PortraitUserListReqVo {

    @ApiModelProperty(value = "结束时间[yyyy-MM-dd]")
    private String endTime;

    @ApiModelProperty(value = "0 现金网，1 信用网")
    private String labelId;

    @ApiModelProperty(value = "商户ID列表")
    private List<String> merchantIds;

    @ApiModelProperty(value = "商户ID取值范围")
    private String merchantIdsScope;

    @ApiModelProperty(value = "排序[升序：asc 降序 desc]")
    private String order;

    @ApiModelProperty(value = "排序:[1:最后下注时间]")
    private String orderBy;

    @ApiModelProperty(value = "当前第几页")
    private String page;

    @ApiModelProperty(value = "多少条数据")
    private String pageSize;

    private String rows;

    @ApiModelProperty(value = "开始时间[yyyy-MM-dd]")
    private String startTime;

    @ApiModelProperty(value = "用户投注标签")
    private List<String> userLevel;

    @ApiModelProperty(value = "用户名")
    private String username;


    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public List<String> getMerchantIds() {
        return merchantIds;
    }

    public void setMerchantIds(List<String> merchantIds) {
        this.merchantIds = merchantIds;
    }

    public String getMerchantIdsScope() {
        return merchantIdsScope;
    }

    public void setMerchantIdsScope(String merchantIdsScope) {
        this.merchantIdsScope = merchantIdsScope;
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

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getRows() {
        return rows;
    }

    public void setRows(String rows) {
        this.rows = rows;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public List<String> getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(List<String> userLevel) {
        this.userLevel = userLevel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
