package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(value = "通用Id vo", description = "通用Id vo")
public class IdReqVo implements Serializable {
    @ApiModelProperty(value = "通用Id参数")
    @NotNull
    @Min(value = 1, message = "最小值为1")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

