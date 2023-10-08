package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 保存标签变更记录reqVO
 * </p>
 *
 */
@ApiModel(value="UserSaveTagChangeReqVo reqVO", description="保存标签变更记录reqVO")
public class UserSaveTagChangeReqVo implements Serializable {

    @ApiModelProperty(value = "用户Id")
    private Long userId;
    @ApiModelProperty(value = "标签id")
    private Integer levelId;
    @ApiModelProperty(value = "变更前标签id")
    private Long changeBefore;
    @ApiModelProperty(value = "变更后标签id")
    private Long changeAfter;
    @ApiModelProperty(value = "标签名称")
    private String levelName;
    @ApiModelProperty(value = "商户编码")
    private String merchantCode;
    @ApiModelProperty(value = "变更理由")
    private String changeReason;
    @ApiModelProperty(value = "变更说明")
    private String changeDetail;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getLevelId() {
        return levelId;
    }

    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
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

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }

    public String getChangeDetail() {
        return changeDetail;
    }

    public void setChangeDetail(String changeDetail) {
        this.changeDetail = changeDetail;
    }
}
