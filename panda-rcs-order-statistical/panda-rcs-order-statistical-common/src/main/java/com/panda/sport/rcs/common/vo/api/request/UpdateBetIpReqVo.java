package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author derre
 * @date 2022-03-29
 */
@ApiModel(value = "修改投注ip的标签 接收 对象", description = "")
public class UpdateBetIpReqVo {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "ip标签 id")
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
