package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "玩家组用户列表 接收 对象", description = "")
public class UserGroupUserReqVo {

    @ApiModelProperty(value = "当前第几页")
    private Integer page;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;

    @ApiModelProperty(value = "用户id")
    private String userIds;

    @ApiModelProperty(value = "row")
    private Integer rows;

    @ApiModelProperty(value = "投注特征类标签")
    private List<Integer> betLabelList;

    @ApiModelProperty(value = "危险IP标签")
    private List<Integer> riskIpLabel;

    @ApiModelProperty(value = "ips")
    private String ips;

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

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public List<Integer> getBetLabelList() {
        return betLabelList;
    }

    public void setBetLabelList(List<Integer> betLabelList) {
        this.betLabelList = betLabelList;
    }

    public List<Integer> getRiskIpLabel() {
        return riskIpLabel;
    }

    public void setRiskIpLabel(List<Integer> riskIpLabel) {
        this.riskIpLabel = riskIpLabel;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }
}
