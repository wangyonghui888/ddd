package com.panda.sport.rcs.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "玩家组预警查询对象")
@Data
public class QueryUserGroupWarnVo {

    @ApiModelProperty(value = "状态，0、未处理 1、已确认 2、已加白")
    private Integer status;

    @ApiModelProperty(value = "商户Id集合")
    private Long [] merchantIds;

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

    @ApiModelProperty(value = "指纹Id")
    private String deviceId;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "玩家组Id")
    private Integer groupId;

    @ApiModelProperty(value = "登录Ip")
    private String loginIp;

    @ApiModelProperty(value = "1:自然日，2账务日")
    private String dataType;

    @ApiModelProperty(value = "日期")
    private String dateId;

    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "商户Id")
    private String merchantId;

    @ApiModelProperty(value = "用户Id集合")
    private Long [] userIdList;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "投注金额")
    private Long betAmount;

    @ApiModelProperty(value = "设备类型，1:H5，2：PC,3:Android,4:IOS")
    private Integer deviceType;

    @ApiModelProperty(value = "赛事长ID")
    private String matchManagerId;

    @ApiModelProperty(value = "危险类型 [1：ARB 2：Goal Bet 3：Group Bet]")
    private String riskType;

    @ApiModelProperty(value = "赛种集合")
    private Integer [] sportIds;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
