package com.panda.sport.rcs.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "玩家组日志列表对外提供")

@Data
public class QueryListByExternalVo {

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "查询开始日期")
    private String startTime;

    @ApiModelProperty(value = "查询结束日期")
    private String endTime;

    private Integer rows;

    @ApiModelProperty(value = "当前第几页")
    private Integer page;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;

    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "商户Code")
    private String merchantCode;

    @ApiModelProperty(value = "新值")
    private String newUserGroupName;

    @ApiModelProperty(value = "原值")
    private String oldUserGroupName;

    @ApiModelProperty(value = "指纹Id")
    private String deviceId;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "用户名")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
