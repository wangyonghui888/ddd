package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author derre
 * @date 2022-03-29
 */
@ApiModel(value = "ip标签管理 接收 对象", description = "")
public class AddIpLabelReqVO {

    @ApiModelProperty(value = "标签id")
    private Long id;

    @ApiModelProperty(value = "标签名称")
    private String ipLabelName;

    @ApiModelProperty(value = "备注")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpLabelName() {
        return ipLabelName;
    }

    public void setIpLabelName(String ipLabelName) {
        this.ipLabelName = ipLabelName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
