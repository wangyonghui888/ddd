package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = " 单个/批量修改IP标签 vo", description = "")
public class UpdateByRiskUserVisitIpReqVo {

    @ApiModelProperty(value = "需要修改的IP")
    private String[] ip;
    @ApiModelProperty(value = "标签ID")
    private Long tagId;

    public String[] getIp() {
        return ip;
    }

    public void setIp(String[] ip) {
        this.ip = ip;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
