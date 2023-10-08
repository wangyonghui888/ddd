package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "删除危险球队 接收 对象", description = "")
public class RiskTeamDelReqVo {

    @ApiModelProperty(value = "球队id")
    @NotNull(message = "球队id不能为空")
    private Long teamId;

    @ApiModelProperty(value = "球队名称")
    @NotNull(message = "球队名称不能为空")
    private String teamName;

    /**
     * 操作人IP
     */
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

}
