package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @author derre
 * @date 2022-04-10
 */
@ApiModel(value = "通过指纹id获取关联用户列表 接收 对象", description = "")
public class FingerprintUserListReqVO {

    @ApiModelProperty(value = "指纹id")
    private String fingerprintId;
    @ApiModelProperty(value = "排序方式:[降序:desc, 升序:asc]")
    private String order;
    @ApiModelProperty(value = "排序字段:[1:危险等级 2:关联用户 3：成功投注金额 4：平台盈利]")
    private Integer orderBy;
    @ApiModelProperty(value = "当前第几页")
    private Integer page;
    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;
    @ApiModelProperty(value = "rows")
    private Integer rows;

    public String getFingerprintId() {
        return fingerprintId;
    }

    public void setFingerprintId(String fingerprintId) {
        this.fingerprintId = fingerprintId;
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
}
