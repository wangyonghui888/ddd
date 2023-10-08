package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "ip设置tag-请求vo", description = "")
public class IpTagSetReqVo {
    @ApiModelProperty(value = "ip")
    @NotNull
    private String ip;
    @ApiModelProperty(value = "标签Id")
    @NotNull
    private long tagId;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }
}
