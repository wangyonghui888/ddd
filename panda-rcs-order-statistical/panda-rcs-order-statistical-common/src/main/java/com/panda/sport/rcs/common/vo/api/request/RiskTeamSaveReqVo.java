package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "新增危险球队 接收 对象", description = "")
public class RiskTeamSaveReqVo {

    @ApiModelProperty(value = "区域id")
    private Integer regionId;

    @ApiModelProperty(value = "危险级别")
    private Integer riskLevel;

    @ApiModelProperty(value = "赛种ID")
    private Integer sportId;

    @ApiModelProperty(value = "是否生效（是/否）")
    private Integer status;

    @ApiModelProperty(value = "赛种英文名")
    private String sportNameEn;

    @ApiModelProperty(value = "赛种中文名")
    private String sportNameCn;

    @ApiModelProperty(value = "球队区域")
    private String teamArea;

    @ApiModelProperty(value = "球队id")
    private Long teamId;

    @ApiModelProperty(value = "模糊搜索球队id或球队名称")
    private String teamLike;

    @ApiModelProperty(value = "球队名称中文")
    private String teamNameCn;

    @ApiModelProperty(value = "球队名称英文")
    private String teamNameEn;

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

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Integer getSportId() {
        return sportId;
    }

    public void setSportId(Integer sportId) {
        this.sportId = sportId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSportNameEn() {
        return sportNameEn;
    }

    public void setSportNameEn(String sportNameEn) {
        this.sportNameEn = sportNameEn;
    }

    public String getTeamArea() {
        return teamArea;
    }

    public void setTeamArea(String teamArea) {
        this.teamArea = teamArea;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getTeamLike() {
        return teamLike;
    }

    public void setTeamLike(String teamLike) {
        this.teamLike = teamLike;
    }

    public String getTeamNameCn() {
        return teamNameCn;
    }

    public void setTeamNameCn(String teamNameCn) {
        this.teamNameCn = teamNameCn;
    }

    public String getTeamNameEn() {
        return teamNameEn;
    }

    public void setTeamNameEn(String teamNameEn) {
        this.teamNameEn = teamNameEn;
    }

    public String getSportNameCn() {
        return sportNameCn;
    }

    public void setSportNameCn(String sportNameCn) {
        this.sportNameCn = sportNameCn;
    }


}
