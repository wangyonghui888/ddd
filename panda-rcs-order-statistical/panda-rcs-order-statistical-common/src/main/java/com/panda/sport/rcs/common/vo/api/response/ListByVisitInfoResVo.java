package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * ip基本信息 返回vo
 * </p>
 *
 * @author Kir
 * @since 2021-01-29
 */
@ApiModel(value = "ip基本信息  返回vo", description = "ip基本信息  返回vo")
public class ListByVisitInfoResVo {

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "用户名称")
    private String area;

    @ApiModelProperty(value = "标签ID")
    private Integer tagId;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }
}
