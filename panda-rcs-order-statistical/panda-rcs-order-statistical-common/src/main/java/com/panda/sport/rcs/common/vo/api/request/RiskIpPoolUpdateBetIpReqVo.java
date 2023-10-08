package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;

@ApiModel(value = "通过ip获取危险ip池用户列表 接收 对象", description = "")
public class RiskIpPoolUpdateBetIpReqVo {

    @ApiModelProperty(value = "要修改的ip")
    @NotBlank(message = "id不能为空")
    private String id;

    @ApiModelProperty(value = "id")
    @NotBlank(message = "ip标签id")
    private String ipLabel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIpLabel() {
        return ipLabel;
    }

    public void setIpLabel(String ipLabel) {
        this.ipLabel = ipLabel;
    }
}
