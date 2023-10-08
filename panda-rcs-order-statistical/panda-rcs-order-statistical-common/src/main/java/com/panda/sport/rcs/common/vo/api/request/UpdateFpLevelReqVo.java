package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @author derre
 * @date 2022-04-10
 */
@ApiModel(value = "修改指纹危险等级 接收 对象", description = "")
public class UpdateFpLevelReqVo {

    @ApiModelProperty(value = "id")
    @NotBlank(message = "id不能为空")
    private String fingerprintId;
    @ApiModelProperty(value = "危险等级")
    private Integer riskLevel;

    public String getFingerprintId() {
        return fingerprintId;
    }

    public void setFingerprintId(String fingerprintId) {
        this.fingerprintId = fingerprintId;
    }

    public Integer getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel) {
        this.riskLevel = riskLevel;
    }
}
