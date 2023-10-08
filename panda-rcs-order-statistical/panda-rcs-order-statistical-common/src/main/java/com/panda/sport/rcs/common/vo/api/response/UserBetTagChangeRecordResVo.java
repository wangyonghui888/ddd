package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 标签变更记录返回 VO
 * </p>
 *
 * @author Kir
 * @since 2021-03-11
 */
@ApiModel(value="UserBetTagChangeRecordResVo Vo对象", description="标签变更记录返回VO")
public class UserBetTagChangeRecordResVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "商户编码")
    private String merchantCode;

    @ApiModelProperty(value = "变更时间")
    private Long changeTime;

    @ApiModelProperty(value = "标签id")
    private Integer tagId;

    @ApiModelProperty(value = "标签名称")
    private String tagName;

    @ApiModelProperty(value = "实际值")
    private String realityValue;

    @ApiModelProperty(value = "变更建议（1.增加 2.取消）")
    private Integer changeSuggest;

    @ApiModelProperty(value = "状态（0.未处理 1.接收 2.忽略）")
    private Integer status;

    @ApiModelProperty(value = "操作时间")
    private Long operateTime;

    @ApiModelProperty(value = "操作人")
    private String changeManner;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "手动修改后的标签id-针对于投注特征标签预警")
    private Long changeTag;

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Long getChangeTag() {
        return changeTag;
    }

    public void setChangeTag(Long changeTag) {
        this.changeTag = changeTag;
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

    public Long getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Long changeTime) {
        this.changeTime = changeTime;
    }

    public Integer getChangeSuggest() {
        return changeSuggest;
    }

    public void setChangeSuggest(Integer changeSuggest) {
        this.changeSuggest = changeSuggest;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getChangeManner() {
        return changeManner;
    }

    public void setChangeManner(String changeManner) {
        this.changeManner = changeManner;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
