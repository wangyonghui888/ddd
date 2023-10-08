package com.panda.sport.rcs.common.vo.api.request;

import com.panda.sport.rcs.common.bean.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(value = "玩家组列表及用户数 入参对象", description = "玩家组列表及用户数 入参对象")
public class UserListReqVo extends PageBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String[] id;

    @ApiModelProperty(value = "ip")
    private String[] ip;

    @ApiModelProperty(value = "tagId")
    private String[] tagId;

    @ApiModelProperty(value = "ipTagId")
    private String[] ipTagId;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String[] getId() {
        return id;
    }

    public void setId(String[] id) {
        this.id = id;
    }

    public String[] getIp() {
        return ip;
    }

    public void setIp(String[] ip) {
        this.ip = ip;
    }

    public String[] getTagId() {
        return tagId;
    }

    public void setTagId(String[] tagId) {
        this.tagId = tagId;
    }

    public String[] getIpTagId() {
        return ipTagId;
    }

    public void setIpTagId(String[] ipTagId) {
        this.ipTagId = ipTagId;
    }
}
