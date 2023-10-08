package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = " 修改IP对应备注信息 vo", description = "")
public class UpdateByRiskUserVisitIpRemakeReqVo {

    @ApiModelProperty(value = "ip")
    private String ip;
    @ApiModelProperty(value = "备注")
    private String remake;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRemake() {
        return remake;
    }

    public void setRemake(String remake) {
        this.remake = remake;
    }
}
