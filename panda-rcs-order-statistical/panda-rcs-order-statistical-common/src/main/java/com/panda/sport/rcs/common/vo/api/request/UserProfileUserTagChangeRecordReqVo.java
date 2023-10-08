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
@ApiModel(value="UserProfileUserTagChangeRecordResVo reqVO", description="标签变更记录reqVO")
public class UserProfileUserTagChangeRecordReqVo extends PageBean implements Serializable {

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "玩家组名称")
    private String[] groupId;

    @ApiModelProperty(value = "变更时间")
    private Long changeTime;

    @ApiModelProperty(value = "变更方式 1自动,2手动")
    private Integer changeType;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String[] getGroupId() {
        return groupId;
    }

    public void setGroupId(String[] groupId) {
        this.groupId = groupId;
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
}
