package com.panda.sport.rcs.common.vo.api.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "用户信息统计 接收 对象", description = "")
public class UserProfileTotalDataReqVo {

    @ApiModelProperty(value = "用户名")
    private String username;
}
