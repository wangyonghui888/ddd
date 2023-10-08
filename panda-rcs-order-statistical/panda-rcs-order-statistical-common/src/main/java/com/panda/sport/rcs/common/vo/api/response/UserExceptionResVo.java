package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 用户异常记录 VO
 * </p>
 *
 * @author skykong
 * @since 2022-05-26
 */
@ApiModel(value="UserExceptionResVo Vo对象", description="用户异常记录 VO")
public class UserExceptionResVo  implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;


    @ApiModelProperty(value = "商户Id")
    private String merchantCode;

    @ApiModelProperty(value = "操作时间")
    private Long operateTime;

    @ApiModelProperty(value = "手动修改后的标签id-针对于投注特征标签预警")
    private Long changeTag;

    @ApiModelProperty(value = "手动修改后的标签名称-针对于投注特征标签预警")
    private String changeTagName;

    @ApiModelProperty(value = "风险类型-根据修改后所用户类型")
    private String riskType;
    @ApiModelProperty(value = "2.投注特征标签")
    private Integer tagType;

    @ApiModelProperty(value = "异常备注")
    private String remark;

    private String remark1;

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {

        this.remark = remark;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Long getChangeTag() {
        return changeTag;
    }

    public void setChangeTag(Long changeTag) {
        this.changeTag = changeTag;
    }

    public String getChangeTagName() {
        return changeTagName;
    }

    public void setChangeTagName(String changeTagName) {
        this.changeTagName = changeTagName;
    }


    public String getRiskType() {
        return riskType;
    }

    public void setRiskType(String riskType) {
        this.riskType = riskType;
    }

    public Integer getTagType() {
        return tagType;
    }

    public void setTagType(Integer tagType) {
        this.tagType = tagType;
    }

}
