package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "增加危险联赛 接收 对象", description = "")
public class TyRiskAddRiskTournamentReqVo {

    @ApiModelProperty(value = "联赛ID")
    private Integer id;

    @ApiModelProperty(value = "区域id")
    private Integer regionId;

    @ApiModelProperty(value = "危险级别")
    private Integer riskLevel;

    @ApiModelProperty(value = "赛种id")
    private Integer sportId;

    @ApiModelProperty(value = "赛种名称")
    private String sportName;

    @ApiModelProperty(value = "是否生效（1 是/ 0否）")
    private String status;

    @ApiModelProperty(value = "联赛名称英文")
    private String tournamentNameEn;

    @ApiModelProperty(value = "联赛名称中文简体")
    private String tournamentNameCn;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTournamentNameEn() {
        return tournamentNameEn;
    }

    public void setTournamentNameEn(String tournamentNameEn) {
        this.tournamentNameEn = tournamentNameEn;
    }

    public String getTournamentNameCn() {
        return tournamentNameCn;
    }

    public void setTournamentNameCn(String tournamentNameCn) {
        this.tournamentNameCn = tournamentNameCn;
    }
}
