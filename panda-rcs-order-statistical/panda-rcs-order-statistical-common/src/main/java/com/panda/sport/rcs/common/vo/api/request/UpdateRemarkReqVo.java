package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

/**
 * @author derre
 * @date 2022-04-10
 */
@ApiModel(value = "修改危险指纹备注 接收 对象", description = "")
public class UpdateRemarkReqVo {

    @ApiModelProperty(value = "主键id")
    @NotBlank(message = "id不能为空")
    private String fingerprintId;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    public String getFingerprintId() {
        return fingerprintId;
    }

    public void setFingerprintId(String fingerprintId) {
        this.fingerprintId = fingerprintId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
