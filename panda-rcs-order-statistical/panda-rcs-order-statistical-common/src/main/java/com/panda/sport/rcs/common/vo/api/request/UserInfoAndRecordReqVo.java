package com.panda.sport.rcs.common.vo.api.request;

import com.panda.sport.rcs.common.bean.PageBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 查看单条投注预警消息入参 reqVO
 * </p>
 *
 * @author Kir
 * @since 2021-03-11
 */
@ApiModel(value="UserInfoAndRecordReqVo reqVO", description="查看单条投注预警消息入参reqVO")
public class UserInfoAndRecordReqVo extends PageBean implements Serializable {

    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "状态（0.未处理 1.接收 2.忽略）")
    private Integer[] status;

    public Integer[] getStatus() {
        return status;
    }

    public void setStatus(Integer[] status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
