package com.panda.sport.rcs.common.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "白名单查询列表")
@Data
public class QueryWhiteVo {

    @ApiModelProperty(value = "排序方式:[降序:desc, 升序:asc]")
    private String order;

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "排序字段:[1:最后下注时间2:投注金额3:平台盈利4:平台盈利率5:平台胜率6:注单数7：操作人 8： 操作时间]")
    private Integer orderBy;

    private Integer rows;

    @ApiModelProperty(value = "查询条件")
    private String search;

    @ApiModelProperty(value = "查询开始日期")
    private String startTime;

    @ApiModelProperty(value = "查询结束日期")
    private String endTime;

    @ApiModelProperty(value = "白名单查询类型 1：用户 2： IP 3： 指纹")
    private Integer queryType;

    @ApiModelProperty(value = "当前第几页")
    private Integer page;

    @ApiModelProperty(value = "多少条数据")
    private Integer pageSize;

}
