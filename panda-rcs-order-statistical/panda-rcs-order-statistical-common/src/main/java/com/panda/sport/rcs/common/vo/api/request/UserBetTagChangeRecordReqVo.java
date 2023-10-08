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
@ApiModel(value="UserBetTagChangeRecordReqVo reqVO", description="标签变更记录reqVO")
public class UserBetTagChangeRecordReqVo extends PageBean implements Serializable {

    @ApiModelProperty(value = "变更时间")
    private Long changeTime;

    @ApiModelProperty(value = "状态（0.未处理 1.接收 2.忽略）")
    private Integer status;

    @ApiModelProperty(value = "标签id")
    private Integer[] tagId;

    public Integer[] getTagId() {
        return tagId;
    }

    public void setTagId(Integer[] tagId) {
        this.tagId = tagId;
    }

    public Long getChangeTime() {
        return changeTime;
    }

    public void setChangeTime(Long changeTime) {
        this.changeTime = changeTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
