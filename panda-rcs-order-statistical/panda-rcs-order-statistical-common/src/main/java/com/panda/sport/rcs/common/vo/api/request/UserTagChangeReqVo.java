package com.panda.sport.rcs.common.vo.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 查询标签变更记录reqVO
 * </p>
 *
 */
@ApiModel(value="UserTagChangeReqVo reqVO", description="查询reqVO")
public class UserTagChangeReqVo implements Serializable {

    @ApiModelProperty(value = "用户Id")
    @NotNull(message = "userId不能为空")
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
