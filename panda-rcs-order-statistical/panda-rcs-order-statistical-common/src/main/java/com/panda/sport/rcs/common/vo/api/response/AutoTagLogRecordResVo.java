package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 系统自动标签日志变更记录返回 VO
 * </p>
 *
 * @author Kir
 * @since 2022-02-26
 */
@ApiModel(value="AutoTagLogRecordResVo Vo对象", description="系统自动标签日志变成记录 VO")
public class AutoTagLogRecordResVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名称")
    private String userName;

    @ApiModelProperty(value = "商户编码")
    private String merchantCode;

    @ApiModelProperty(value = "操作人")
    private String changeManner;

    @ApiModelProperty(value = "操作时间")
    private Long operateTime;

    @ApiModelProperty(value = "手动修改后的标签id-针对于投注特征标签预警")
    private Long changeTag;

    @ApiModelProperty(value = "手动修改后的标签名称-针对于投注特征标签预警")
    private String changeTagName;

    @ApiModelProperty(value = "手动修改前的标签id-针对于投注特征标签预警")
    private Long beforeTagId;

    @ApiModelProperty(value = "手动修改前的标签名称-针对于投注特征标签预警")
    private String beforeTagName;

    @ApiModelProperty(value = "2.投注特征标签 ")
    private Integer tagType;

    @ApiModelProperty(value = "备注")
    private String realityValue;

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getChangeManner() {
        return changeManner;
    }

    public void setChangeManner(String changeManner) {
        this.changeManner = changeManner;
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

    public Long getBeforeTagId() {
        return beforeTagId;
    }

    public void setBeforeTagId(Long beforeTagId) {
        this.beforeTagId = beforeTagId;
    }

    public String getBeforeTagName() {
        return beforeTagName;
    }

    public void setBeforeTagName(String beforeTagName) {
        this.beforeTagName = beforeTagName;
    }

    public Integer getTagType() {
        return tagType;
    }

    public void setTagType(Integer tagType) {
        this.tagType = tagType;
    }

    public String getRealityValue() {
        if (realityValue != null) {
            realityValue = realityValue.replace("\n", "").replace("\r", "").replace("\t", "");
            int begin = realityValue.indexOf("[{\"result\":\"") + 12;
            int end = realityValue.indexOf("@;@");
            if (begin > 11 && end > -1) {
                String data = realityValue.substring(begin,end );
                data=data.replace("\"", "\\\"");
                realityValue = realityValue.substring(0, begin )+ data + realityValue.substring(end);
            }
        }
        return realityValue;
    }

    public void setRealityValue(String realityValue) {
        this.realityValue = realityValue;
    }
}
