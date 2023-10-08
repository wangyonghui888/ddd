package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "危险等级更改 接收 对象", description = "")
public class TyRiskUpdateLevelTournamentReqVo {

    @ApiModelProperty(value = "联赛ID")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "联赛名称")
    @NotNull(message = "联赛名称不能为空")
    private String name;

    @ApiModelProperty(value = "危险级别")
    private Integer riskLevel;

    @ApiModelProperty(value = "原危险级别")
    private Integer oldRiskLevel;

    @ApiModelProperty(value = "是否生效（1 是/ 0否）")
    private Integer status;

    @ApiModelProperty(value = "原值（1 是/ 0否）")
    private Integer oldStatus;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getOldRiskLevel() {
        return oldRiskLevel;
    }

    public void setOldRiskLevel(Integer oldRiskLevel) {
        this.oldRiskLevel = oldRiskLevel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(Integer oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
