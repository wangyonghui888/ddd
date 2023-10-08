package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @author derre
 * @date 2022-04-10
 */
@ApiModel(value = "删除指纹 接收 对象", description = "")
public class DeleteFpReqVo {

    @ApiModelProperty(value = "id")
    @NotBlank(message = "id不能为空")
    private String fingerprintId;

    public String getFingerprintId() {
        return fingerprintId;
    }

    public void setFingerprintId(String fingerprintId) {
        this.fingerprintId = fingerprintId;
    }
}
