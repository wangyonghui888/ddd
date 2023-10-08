package com.panda.sport.rcs.common.vo.api.request;

import com.panda.sport.rcs.common.bean.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 标签变更记录 reqVO
 * </p>
 *
 * @author Kir
 * @since 2021-03-11
 */
@ApiModel(value="AutoTagLogRecordReqVo reqVO", description="标签变更记录reqVO")
public class AutoTagLogRecordReqVo extends PageBean implements Serializable {

    @ApiModelProperty(value = "userId")
    private Long userId;

    @ApiModelProperty(value = "开始时间")
    private Long startTime;

    @ApiModelProperty(value = "结束时间")
    private Long endTime;

    @ApiModelProperty(value = "2.投注特征标签 ")
    private Integer changeLogType;

    @ApiModelProperty(value = "商户编码")
    private String merchantCode;

    @ApiModelProperty(value = "操作人")
    private String changeManner;

    @ApiModelProperty(value = "原值tags")
    private Integer[] beforeTags;

    @ApiModelProperty(value = "新值tags")
    private Integer[] changeTags;

    public String getChangeManner() {
        return changeManner;
    }

    public void setChangeManner(String changeManner) {
        this.changeManner = changeManner;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Integer getChangeLogType() {
        return changeLogType;
    }

    public void setChangeLogType(Integer changeLogType) {
        this.changeLogType = changeLogType;
    }

    public Integer[] getBeforeTags() {
        return beforeTags;
    }

    public void setBeforeTags(Integer[] beforeTags) {
        this.beforeTags = beforeTags;
    }

    public Integer[] getChangeTags() {
        return changeTags;
    }

    public void setChangeTags(Integer[] changeTags) {
        this.changeTags = changeTags;
    }
}
