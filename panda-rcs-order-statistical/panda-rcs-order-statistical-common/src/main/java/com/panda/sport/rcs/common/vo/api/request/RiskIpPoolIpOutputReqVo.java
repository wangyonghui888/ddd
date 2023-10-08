package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "通过ip获取危险ip池用户列表导出 接收 对象", description = "")
public class RiskIpPoolIpOutputReqVo {


    @ApiModelProperty(value = "排序字段:[1:成功投注金额,2:平台盈利,3:平台盈利率,4:平台胜率]")
    private Integer orderBy;

    @ApiModelProperty(value = "排序方式:[降序:desc, 升序:asc]")
    private String order;

    @ApiModelProperty(value = "ip地址")
    @NotBlank(message = "ip不能为空")
    private String ip;

    @ApiModelProperty(value = "汇率")
    private Boolean isAddRate = false;

    public Integer getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Integer orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Boolean getIsAddRate() {
        return isAddRate;
    }

    public void setIsAddRate(Boolean addRate) {
        this.isAddRate = addRate;
    }
}

