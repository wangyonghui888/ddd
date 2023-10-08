package com.panda.sport.rcs.common.vo.api.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 查看单条投注预警消息入参 resVo
 * </p>
 *
 * @author Kir
 * @since 2021-03-11
 */
@ApiModel(value="UserInfoAndRecordReqVo resVo", description="查看单条投注预警消息入参resVo")
public class UserInfoAndRecordResVo implements Serializable {
    @ApiModelProperty(value = "预警消息集合")
    private IPage<UserBetTagChangeRecordResVo> recordResVos;

    @ApiModelProperty(value = "用户相关休息")
    private UserInfoResVo userInfoResVo;

    public IPage<UserBetTagChangeRecordResVo> getRecordResVos() {
        return recordResVos;
    }

    public void setRecordResVos(IPage<UserBetTagChangeRecordResVo> recordResVos) {
        this.recordResVos = recordResVos;
    }

    public UserInfoResVo getUserInfoResVo() {
        return userInfoResVo;
    }

    public void setUserInfoResVo(UserInfoResVo userInfoResVo) {
        this.userInfoResVo = userInfoResVo;
    }
}
