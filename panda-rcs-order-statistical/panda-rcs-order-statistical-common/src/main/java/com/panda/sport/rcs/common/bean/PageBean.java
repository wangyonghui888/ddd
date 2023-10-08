package com.panda.sport.rcs.common.bean;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 分页参数
 *
 * @author lithan
 */
public class PageBean implements Serializable {

    @ApiModelProperty(value = "第几页")
    @Min(value = 1, message = "最小为1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页多少条")
    @Min(value = 1, message = "最小为1")
    private Integer pageSize = 10;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
