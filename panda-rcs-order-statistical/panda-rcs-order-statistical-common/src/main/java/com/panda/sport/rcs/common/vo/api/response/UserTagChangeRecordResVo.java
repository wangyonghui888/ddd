package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 用户id查询标签查询 VO
 * </p>
 *
 * @author Kir
 * @since 2021-03-11
 */
@ApiModel(value="UserTagChangeRecordResVo Vo对象", description="用户id查询标签查询VO")
public class UserTagChangeRecordResVo {

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "变更时间")
    private Long changeTime;

    @ApiModelProperty(value = "变更方式 1自动,2手动")
    private Integer changeType;

    @ApiModelProperty(value = "变更人(手动情况下)")
    private String changeManner;

    @ApiModelProperty(value = "变更前标签")
    private Long changeBefore;

    @ApiModelProperty(value = "变更后标签")
    private Long changeAfter;

    @ApiModelProperty(value = "变更说明")
    private String changeDetail;

    @ApiModelProperty(value = "变更值 记录每个规则的真实值")
    private String changeValue;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "标签类型  1基本属性类 2投注特征类 3访问特征类 4财务特征类")
    private Integer tagType;

    @ApiModelProperty(value = "变更理由")
    private String changeReason;

    @ApiModelProperty(value = "商户编码")
    private String merchantCode;

    @ApiModelProperty(value = "实际值")
    private String realityValue;

    @ApiModelProperty(value = "变更建议（1.增加 2.取消）")
    private String changeSuggest;

    @ApiModelProperty(value = "状态（0.未处理 1.接收 2.忽略）")
    private Integer status;

    @ApiModelProperty(value = "操作时间")
    private Long operateTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "手动修改后的标签id-针对于投注特征标签预警")
    private Long changeTag;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Long changeTime) {
        this.changeTime = changeTime;
    }

    public Integer getChangeType() {
        return changeType;
    }

    public void setChangeType(Integer changeType) {
        this.changeType = changeType;
    }

    public String getChangeManner() {
        return changeManner;
    }

    public void setChangeManner(String changeManner) {
        this.changeManner = changeManner;
    }

    public Long getChangeBefore() {
        return changeBefore;
    }

    public void setChangeBefore(Long changeBefore) {
        this.changeBefore = changeBefore;
    }

    public Long getChangeAfter() {
        return changeAfter;
    }

    public void setChangeAfter(Long changeAfter) {
        this.changeAfter = changeAfter;
    }

    public String getChangeDetail() {
        return changeDetail;
    }

    public void setChangeDetail(String changeDetail) {
        this.changeDetail = changeDetail;
    }

    public String getChangeValue() {
        return changeValue;
    }

    public void setChangeValue(String changeValue) {
        this.changeValue = changeValue;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getTagType() {
        return tagType;
    }

    public void setTagType(Integer tagType) {
        this.tagType = tagType;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
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

    public String getChangeSuggest() {
        return changeSuggest;
    }

    public void setChangeSuggest(String changeSuggest) {
        this.changeSuggest = changeSuggest;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getChangeTag() {
        return changeTag;
    }

    public void setChangeTag(Long changeTag) {
        this.changeTag = changeTag;
    }
}
