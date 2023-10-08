package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "删除危险联赛 接收 对象", description = "")
public class TyRiskDelRiskTournamentReqVo {

    @ApiModelProperty(value = "联赛ID")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "联赛名称")
    @NotNull(message = "联赛名称不能为空")
    private String name;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

