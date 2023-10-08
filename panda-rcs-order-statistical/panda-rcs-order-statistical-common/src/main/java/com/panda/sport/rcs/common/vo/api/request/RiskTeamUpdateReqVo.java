package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "危险球队信息危险等级修改 接收 对象", description = "")
public class RiskTeamUpdateReqVo {

    @ApiModelProperty(value = "危险级别")
    private Integer riskLevel;

    @ApiModelProperty(value = "是否生效（是/否）")
    private Integer status;


    @ApiModelProperty(value = "原危险级别")
    private Integer oldRiskLevel;


    @ApiModelProperty(value = "原值（1 是/ 0否）")
    private Integer oldStatus;

    @ApiModelProperty(value = "球队id")
    private Long teamId;

    @ApiModelProperty(value = "球队名称")
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

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public Integer getOldRiskLevel() {
        return oldRiskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getOldStatus() {
        return oldStatus;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
}
