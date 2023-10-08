package com.panda.sport.rcs.common.vo.api.request;

import com.panda.sport.rcs.common.bean.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 标签变更记录 reqVO
 * </p>
 *
 * @author Kir
 * @since 2021-03-11
 */
@ApiModel(value="UserExceptionReqVo reqVO", description="用户异常记录reqVO")
public class UserExceptionReqVo extends PageBean implements Serializable {

    @ApiModelProperty(value = "userId")
    private String userId;

    @ApiModelProperty(value = "用户名称",hidden = true)
    private String userName;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "商户Id")
    private String merchantCode;

    @ApiModelProperty(value = "异常用户类型：0 查询全部 1、观察名单  2、异常会员 ")
    @NotNull(message ="异常用户类型不能为空" )
    private Integer userType;

    @ApiModelProperty(value = "语言版本:en ch ")
    @NotNull(message ="语言版本不能为空" )
    private String language;
//
//    @ApiModelProperty(value = "新值tags")
//    private Integer[] changeTags;


    @ApiModelProperty(value = "限制标签由后台定义",hidden = true)
    private String[] nameTags;


    @ApiModelProperty(value = "商户数据权限：当前商户和商户下得数据由业务方提供")
    private String[] merchantCodes;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

//    public Integer[] getBeforeTags() {
//        return beforeTags;
//    }
//
//    public void setBeforeTags(Integer[] beforeTags) {
//        this.beforeTags = beforeTags;
//    }
//
//    public Integer[] getChangeTags() {
//        return changeTags;
//    }
//
//    public void setChangeTags(Integer[] changeTags) {
//        this.changeTags = changeTags;
//    }

    public String[] getNameTags() {
        return nameTags;
    }

    public void setNameTags(String[] nameTags) {
        this.nameTags = nameTags;
    }


    public String[] getMerchantCodes() {
        return merchantCodes;
    }

    public void setMerchantCodes(String[] merchantCodes) {
        this.merchantCodes = merchantCodes;
    }
}
