package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

@ApiModel(value = "危险联赛有效状态更改 接收 对象", description = "")
public class TyRiskUpdateStatusLevelTournamentReqVo {

    @ApiModelProperty(value = "联赛ID")
    @NotNull(message = "id不能为空")
    private Long id;


    @ApiModelProperty(value = "是否生效（1 是/ 0否）")
    private Integer status;


    @ApiModelProperty(value = "原值")
    private Integer oldStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
