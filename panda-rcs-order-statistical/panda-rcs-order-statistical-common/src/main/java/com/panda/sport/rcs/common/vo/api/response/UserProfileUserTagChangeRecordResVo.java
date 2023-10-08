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
@ApiModel(value="UserProfileUserTagChangeRecordResVo Vo对象", description="标签变更记录返回VO")
public class UserProfileUserTagChangeRecordResVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private String userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "商户编码")
    private String merchantCode;

    @ApiModelProperty(value = "玩家组名称")
    private String groupName;

    @ApiModelProperty(value = "变更前标签")
    private String changeBefore;

    @ApiModelProperty(value = "变更后标签")
    private String changeAfter;

    @ApiModelProperty(value = "变更时间")
    private Long changeTime;

    @ApiModelProperty(value = "变更时间(yyyy-MM-dd HH:mm:ss)")
    private String changeTimeForDate;

    @ApiModelProperty(value = "变更方式 1自动,2手动")
    private Integer changeType;

    @ApiModelProperty(value = "变更人(手动情况下)")
    private String changeManner;

    @ApiModelProperty(value = "变更理由")
    private String changeReason;

    public String getChangeTimeForDate() {
        return changeTimeForDate;
    }

    public void setChangeTimeForDate(String changeTimeForDate) {
        this.changeTimeForDate = changeTimeForDate;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getChangeBefore() {
        return changeBefore;
    }

    public void setChangeBefore(String changeBefore) {
        this.changeBefore = changeBefore;
    }

    public String getChangeAfter() {
        return changeAfter;
    }

    public void setChangeAfter(String changeAfter) {
        this.changeAfter = changeAfter;
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

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }
}
