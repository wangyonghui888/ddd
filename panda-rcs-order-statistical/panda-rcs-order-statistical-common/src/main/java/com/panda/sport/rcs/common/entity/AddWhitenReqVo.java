package com.panda.sport.rcs.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "白名单新增对象")
@Data
public class AddWhitenReqVo {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "白名单类型 1：用户, 2： IP, 3： 指纹")
    private Integer queryType;

    @ApiModelProperty(value = "新增的value")
    private String search;

    @ApiModelProperty(value = "用户Id集合")
    private Long [] userIds;

}
