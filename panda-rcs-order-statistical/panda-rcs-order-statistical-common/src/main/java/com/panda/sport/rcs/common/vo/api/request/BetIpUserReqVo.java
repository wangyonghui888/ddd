package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


/**
 * @author derre
 * @date 2022-03-29
 */
@ApiModel(value = "通过ip获取投注IP管理用户列表 接收 对象", description = "")
public class BetIpUserReqVo {

    @ApiModelProperty(value = "成功投注金额")
    private String ip;

    @ApiModelProperty(value = "排序方式:[降序:desc, 升序:asc]")
    private String order;

    @ApiModelProperty(value = "排序字段:[1:成功投注金额,2:平台盈利,3:平台盈利率,4:平台胜率]")
    private Integer orderBy;

    @ApiModelProperty(value = "rows")
    private Integer rows;

    @ApiModelProperty(value = "当前第几页")
    private Integer page;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
