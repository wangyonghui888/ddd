package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author derre
 * @date 2022-03-29
 */
@ApiModel(value = "投注ip池 接收 对象", description = "")
public class BetIpReqVo {

    @ApiModelProperty(value = "成功投注金额")
    private String betAmount;

    @ApiModelProperty(value = "查询结束日期")
    @NotBlank(message = "结束日期不能为空")
    private String endTime;

    @ApiModelProperty(value = "成功投注金额")
    private String ip;

    @ApiModelProperty(value = "ip标签集合")
    private List<Integer> ipLabels;

    @ApiModelProperty(value = "排序方式:[降序:desc, 升序:asc]")
    private String order;

    @ApiModelProperty(value = "排序字段:[1:地区,2:ip标签,3:关联用户,4:成功投注金额,5:平台盈利,6:平台盈利率,7:平台胜率,8:最后下注时间]")
    private Integer orderBy;

    @ApiModelProperty(value = "rows")
    private Integer rows;

    @ApiModelProperty(value = "查询开始日期")
    @NotBlank(message = "开始日期不能为空")
    private String startTime;

    @ApiModelProperty(value = "关联用户id")
    private String userIds;

    @ApiModelProperty(value = "当前第几页")
    private Integer page;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;

    public String getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(String betAmount) {
        this.betAmount = betAmount;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<Integer> getIpLabels() {
        return ipLabels;
    }

    public void setIpLabels(List<Integer> ipLabels) {
        this.ipLabels = ipLabels;
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

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
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
}
