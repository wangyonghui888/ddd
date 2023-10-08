package com.panda.sport.rcs.common.vo.api.request;

import com.panda.sport.rcs.common.bean.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "根据IP查询所有关联用户 vo对象", description = "")
public class UserStatisticReqVo extends PageBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ip")
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
