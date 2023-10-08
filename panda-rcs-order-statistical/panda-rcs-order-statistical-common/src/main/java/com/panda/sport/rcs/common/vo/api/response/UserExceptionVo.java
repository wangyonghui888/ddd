package com.panda.sport.rcs.common.vo.api.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 用户异常记录 VO
 * </p>
 *
 * @author skykong
 * @since 2022-05-26
 */
@ApiModel(value="UserExceptionVo Vo对象", description="用户异常记录 VO")
public class UserExceptionVo implements Serializable {
    @ApiModelProperty(value = "总条数")
    private Long total;

    @ApiModelProperty(value = "异常用户数组")
    private List<UserExceptionResVo> userExceptionResVoList;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<UserExceptionResVo> getUserExceptionResVoList() {
        return userExceptionResVoList;
    }

    public void setUserExceptionResVoList(List<UserExceptionResVo> userExceptionResVoList) {
        this.userExceptionResVoList = userExceptionResVoList;
    }
}
