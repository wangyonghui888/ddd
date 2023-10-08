package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

@ApiModel(value = " 访问特征详情 返回vo", description = "")
public class AccessListResVo {
    @ApiModelProperty(value = "用户Id")
    private String userId;

    @ApiModelProperty(value = "risk_user_visit_ip_tag表Id")
    private Long tagId;

    @ApiModelProperty(value = "登录日期")
    private Long loginDate;

    @ApiModelProperty(value = "ip地址")
    private String ip;

    @ApiModelProperty(value = "地区")
    private String area;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "是否单日异地登录 1是  0否")
    private int isAnotherPlace;

    @ApiModelProperty(value = "关联账户数量")
    private int relationUserNum;

    @ApiModelProperty(value = "标签名称")
    private String tagName = "";

    @ApiModelProperty(value = "关联用户ID列表")
    List<UserResVo> relationUserList = new ArrayList<>();

    public List<UserResVo> getRelationUserList() {
        return relationUserList;
    }

    public void setRelationUserList(List<UserResVo> relationUserList) {
        this.relationUserList = relationUserList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Long getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Long loginDate) {
        this.loginDate = loginDate;
    }

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getIsAnotherPlace() {
        return isAnotherPlace;
    }

    public void setIsAnotherPlace(int isAnotherPlace) {
        this.isAnotherPlace = isAnotherPlace;
    }

    public int getRelationUserNum() {
        return relationUserNum;
    }

    public void setRelationUserNum(int relationUserNum) {
        this.relationUserNum = relationUserNum;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
